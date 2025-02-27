package ua.lpnu.shuliak.lab1.service

import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ParallelPerfectNumberService(
    cpuUsagePercent: Double
) : AbstractPerfectNumberService() {

    private val threadPool = Executors.newFixedThreadPool(
        (Runtime.getRuntime().availableProcessors() * cpuUsagePercent).toInt()
    )

    override fun findPerfectNumbers(limit: Int): List<Int> {
        val perfectNumbers = ConcurrentLinkedQueue<Int>()
        val counter = AtomicInteger(2)

        val tasks = List(Runtime.getRuntime().availableProcessors()) {
            Callable {
                while (perfectNumbers.size < limit) {
                    val num = counter.getAndIncrement()
                    if (isPerfectNumber(num)) {
                        perfectNumbers.add(num)
                    }
                }
            }
        }

        threadPool.invokeAll(tasks)
        return perfectNumbers.sorted()
    }
}