package ua.lpnu.shuliak.lab1.service

import kotlin.math.sqrt

abstract class AbstractPerfectNumberService {

    protected fun isPerfectNumber(number: Int): Boolean {
        var sum = 1
        (2..sqrt(number.toDouble()).toInt())
            .forEach { i ->
                if (number % i == 0) {
                    sum += i + number / i
                }
            }
        return sum == number && number != 1
    }

    abstract fun findPerfectNumbers(limit: Int): List<Int>
}