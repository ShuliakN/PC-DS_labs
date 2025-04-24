package ua.lpnu.shuliak.lab3

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object PriceCalculator {
    fun calculatePrice(flight: Flight, withBaggage: Boolean, withPriority: Boolean): Double {
        var price = flight.basePrice

        val seatFactor = flight.bookedSeats.toDouble() / flight.totalSeats
        price += price * seatFactor * 0.5

        val daysToFlight = ChronoUnit.DAYS.between(LocalDate.now(), flight.date).coerceAtLeast(0)
        if (daysToFlight < 7) {
            price += price * 0.4
        } else if (daysToFlight < 14) {
            price += price * 0.2
        }

        if (withBaggage) price += 30.0
        if (withPriority) price += 20.0

        return "%.2f".format(price).toDouble()
    }
}