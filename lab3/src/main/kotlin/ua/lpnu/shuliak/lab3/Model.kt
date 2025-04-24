package ua.lpnu.shuliak.lab3

import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap


data class Flight(
    val id: String = UUID.randomUUID().toString(),
    val origin: String,
    val destination: String,
    val date: LocalDate,
    val basePrice: Double,
    val totalSeats: Int,
    var bookedSeats: Int = 0
)

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

val flights = HashMap<String, Flight>()
val bookings = HashMap<String, Booking>()