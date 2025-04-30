package ua.lpnu.shuliak.flightservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.lang.Thread.sleep
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit

@SpringBootApplication
class FlightServiceApplication

fun main(args: Array<String>) {
    runApplication<FlightServiceApplication>(*args)
}

@Configuration
class Config {
    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule.Builder().build())
        return mapper
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(redisConnectionFactory)

		val stringSerializer = StringRedisSerializer()
		val jsonSerializer = GenericJackson2JsonRedisSerializer()

		template.keySerializer = stringSerializer
		template.valueSerializer = jsonSerializer
		template.hashKeySerializer = stringSerializer
		template.hashValueSerializer = jsonSerializer

        return template
    }
}

data class Flight(
	val origin: String = "",
    val id: String = UUID.randomUUID().toString(),
    val destination: String ="",
	val date: LocalDate? = null,
	val basePrice: Double = 0.0,
	val totalSeats: Int = 0,
	var bookedSeats: Int = 0
)


@Component
class FlightCachedRepository(
    private val flightRepository: FlightRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun save(flight: Flight) {
        flightRepository.save(flight)
        redisTemplate.opsForValue().set("flight:${flight.id}", objectMapper.writeValueAsString(flight))
    }

    fun findById(id: String): Flight? {
        val key = "flight:$id"
        return redisTemplate.opsForValue().get(key)?.let { objectMapper.readValue<Flight>(it) }
            ?: flightRepository.findById(id)?.also {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(it), 5, TimeUnit.MINUTES)
            }

    }

    fun findAll(): List<Flight> = flightRepository.findAll()
}

@Component
class FlightRepository() {
    private val flights = HashMap<String, Flight>()

    fun save(flight: Flight) {
        flights[flight.id] = flight
		longExecution()
    }

    fun findAll(): List<Flight> = flights.values.toList().also { longExecution() }

    fun findById(id: String): Flight? = flights[id].also { longExecution() }

	private fun longExecution() {
		println("long execution")
		sleep(3000)
	}
}

@RestController
@RequestMapping("/api/flights")
class FlightController(
    private val flightRepository: FlightCachedRepository
) {

    @PostMapping
    fun addFlight(@RequestBody flight: Flight): Flight {
        flightRepository.save(flight)
        return flight
    }

    @GetMapping
    fun getFlights(): Collection<Flight> = flightRepository.findAll()

    @GetMapping("/{id}")
    fun getFlightById(@PathVariable id: String): Flight? =
        flightRepository.findById(id)

    @PostMapping("/{id}/increment")
    fun incrementSeats(@PathVariable id: String) {
        val flight = flightRepository.findById(id) ?: throw IllegalArgumentException("Flight not found")
        if (flight.bookedSeats >= flight.totalSeats) throw IllegalStateException("Flight full")
        flight.bookedSeats += 1
    }
}