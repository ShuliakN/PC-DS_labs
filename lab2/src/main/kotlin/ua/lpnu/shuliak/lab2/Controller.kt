package ua.lpnu.shuliak.lab2

import org.springframework.web.bind.annotation.RestController


import org.springframework.web.bind.annotation.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api")
class BookClubController {
    private val books = HashMap<String, Book>()
    private val reviews = HashMap<String, Review>()
    private val meetings = HashMap<String, Meeting>()

    @GetMapping("/books")
    fun getBooks() = books.values

    @PostMapping("/books")
    fun addBook(@RequestBody book: Book): Book {
        books[book.id] = book
        return book
    }

    @PutMapping("/books/{id}")
    fun updateBook(@PathVariable id: String, @RequestBody updated: Book): Book? {
        return if (books.containsKey(id)) {
            val book = updated.copy(id = id)
            books[id] = book
            book
        } else null
    }

    @DeleteMapping("/books/{id}")
    fun deleteBook(@PathVariable id: String) = books.remove(id)

    @GetMapping("/reviews")
    fun getReviews() = reviews.values

    @PostMapping("/reviews")
    fun addReview(@RequestBody review: Review): Review {
        reviews[review.id] = review
        return review
    }

    @GetMapping("/meetings")
    fun getMeetings() = meetings.values

    @PostMapping("/meetings")
    fun addMeeting(@RequestBody meeting: Meeting): Meeting? {
        if (meeting.bookIds.all { books.containsKey(it) }) {
            meetings[meeting.id] = meeting
            return meeting
        }
        return null
    }

    @DeleteMapping("/meetings/{id}")
    fun deleteMeeting(@PathVariable id: String) = meetings.remove(id)
}
