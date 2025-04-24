package ua.lpnu.shuliak.bookingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@SpringBootApplication
class BookingServiceApplication{
	@Bean
	fun restTemplate() = RestTemplate()
}

fun main(args: Array<String>) {
	runApplication<BookingServiceApplication>(*args)
}


data class Booking(
	val id: String = UUID.randomUUID().toString(),
	val flightId: String,
	val withBaggage: Boolean,
	val withPriority: Boolean,
	val totalPrice: Double
)

data class BookingRequest(
	val flightId: String,
	val withBaggage: Boolean = false,
	val withPriority: Boolean = false
)

data class Flight(
	val id: String,
	val origin: String,
	val destination: String,
	val date: LocalDate,
	val basePrice: Double,
	val totalSeats: Int,
	val bookedSeats: Int
)

val bookings = HashMap<String, Booking>()

@RestController
@RequestMapping("/api/bookings")
class BookingController(val restTemplate: RestTemplate) {

	@PostMapping
	fun bookFlight(@RequestBody request: BookingRequest): Booking {
		val flight = restTemplate.getForObject(
			"http://localhost:8081/api/flights/${request.flightId}",
			Flight::class.java
		) ?: throw IllegalArgumentException("Flight not found")

		if (flight.bookedSeats >= flight.totalSeats) throw IllegalStateException("Flight full")

		val price = PriceCalculator.calculatePrice(flight, request.withBaggage, request.withPriority)

		restTemplate.postForObject("http://localhost:8081/api/flights/${request.flightId}/increment", null, Void::class.java)

		val booking = Booking(
			flightId = flight.id,
			withBaggage = request.withBaggage,
			withPriority = request.withPriority,
			totalPrice = price
		)

		bookings[booking.id] = booking
		return booking
	}

	@GetMapping
	fun getAllBookings(): Collection<Booking> = bookings.values
}

object PriceCalculator {
	fun calculatePrice(flight: Flight, withBaggage: Boolean, withPriority: Boolean): Double {
		var price = flight.basePrice

		val seatFactor = flight.bookedSeats.toDouble() / flight.totalSeats
		price += price * seatFactor * 0.5

		val daysToFlight = ChronoUnit.DAYS.between(LocalDate.now(), flight.date).coerceAtLeast(0)
		if (daysToFlight < 7) price += price * 0.4
		else if (daysToFlight < 14) price += price * 0.2

		if (withBaggage) price += 30.0
		if (withPriority) price += 20.0

		return "%.2f".format(price).toDouble()
	}
}