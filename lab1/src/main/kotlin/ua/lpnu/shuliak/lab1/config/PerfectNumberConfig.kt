package ua.lpnu.shuliak.lab1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua.lpnu.shuliak.lab1.service.ParallelPerfectNumberService
import ua.lpnu.shuliak.lab1.service.SyncPerfectNumberService

@Configuration
class PerfectNumberConfig(
    @Value("\${perfectNumber.parallelism.cpuUsagePercent:0}")
    private val cpuUsagePercent: Double,
    @Value("\${perfectNumber.parallelism.enabled:true}")
    private val parallelEnabled: Boolean
) {
    @Bean
    fun createPerfectNumberService() =
        ParallelPerfectNumberService(cpuUsagePercent).takeIf { parallelEnabled} ?: SyncPerfectNumberService()

}