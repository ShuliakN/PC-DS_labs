package ua.lpnu.shuliak.flightservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@SpringBootApplication
class FlightServiceApplication

fun main(args: Array<String>) {
	runApplication<FlightServiceApplication>(*args)
}

data class Flight(
	val id: String = UUID.randomUUID().toString(),
	val origin: String,
	val destination: String,
	val date: LocalDate,
	val basePrice: Double,
	val totalSeats: Int,
	var bookedSeats: Int = 0
)

val flights = HashMap<String, Flight>()

@RestController
@RequestMapping("/api/flights")
class FlightController {

	@PostMapping
	fun addFlight(@RequestBody flight: Flight): Flight {
		flights[flight.id] = flight
		return flight
	}

	@GetMapping
	fun getFlights(): Collection<Flight> = flights.values

	@GetMapping("/{id}")
	fun getFlightById(@PathVariable id: String): Flight? =
		flights[id]

	@PostMapping("/{id}/increment")
	fun incrementSeats(@PathVariable id: String) {
		val flight = flights[id] ?: throw IllegalArgumentException("Flight not found")
		if (flight.bookedSeats >= flight.totalSeats) throw IllegalStateException("Flight full")
		flight.bookedSeats += 1
	}
}