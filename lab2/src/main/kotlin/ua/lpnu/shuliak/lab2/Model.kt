package ua.lpnu.shuliak.lab2

import java.util.*

data class Book(val id: String = UUID.randomUUID().toString(), val title: String, val author: String, val genre: String)
data class Review(val id: String = UUID.randomUUID().toString(), val bookId: String, val rating: Int, val comment: String)
data class Meeting(val id: String = UUID.randomUUID().toString(), val date: String, val topic: String, val bookIds: List<String>)
