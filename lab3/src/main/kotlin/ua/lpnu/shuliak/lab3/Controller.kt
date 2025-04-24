package ua.lpnu.shuliak.lab3

import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class LowCostController {

    @PostMapping("/flights")
    fun addFlight(@RequestBody flight: Flight): Flight {
        flights[flight.id] = flight
        return flight
    }

    @GetMapping("/flights")
    fun getFlights(): Collection<Flight> = flights.values

    @PostMapping("/bookings")
    fun bookFlight(@RequestBody request: BookingRequest): Booking {
        val flight = flights[request.flightId] ?: throw IllegalArgumentException("Flight not found")
        if (flight.bookedSeats >= flight.totalSeats) throw IllegalStateException("Flight full")

        val totalPrice = PriceCalculator.calculatePrice(flight, request.withBaggage, request.withPriority)
        flight.bookedSeats += 1

        val booking = Booking(
            flightId = request.flightId,
            withBaggage = request.withBaggage,
            withPriority = request.withPriority,
            totalPrice = totalPrice
        )
        bookings[booking.id] = booking
        return booking
    }

    @GetMapping("/bookings")
    fun getBookings(): Collection<Booking> = bookings.values
}