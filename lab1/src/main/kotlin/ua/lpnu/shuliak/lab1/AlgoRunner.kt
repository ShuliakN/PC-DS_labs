package ua.lpnu.shuliak.lab1

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import ua.lpnu.shuliak.lab1.service.AbstractPerfectNumberService
import java.time.Instant

@Service
class AlgoRunner(
    @Value("\${perfectNumber.limit}")
    private val limit : Int,
    private val perfectNumberService: AbstractPerfectNumberService,
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val res = mutableListOf<Int>()
        val executionTime = measureExecutionTime {
            res.addAll(perfectNumberService.findPerfectNumbers(limit))
        }
        println("Found ${res.size} perfect numbers in $executionTime ms, perfect numbers: ${res.joinToString(", ")}")
    }

    private fun measureExecutionTime(task: () -> Unit): Long {
        val start = Instant.now()
        task()
        val end = Instant.now()
        return end.toEpochMilli() - start.toEpochMilli()
    }
}
