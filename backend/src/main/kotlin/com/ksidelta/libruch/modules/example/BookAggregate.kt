package com.ksidelta.libruch.modules.example


import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import java.util.*

class BookAggregate {
    @AggregateIdentifier
    lateinit var bookId: UUID

    var bookState = BookState.AVAILABLE
    var borrower: Party? = null

    @CommandHandler
    constructor(command: RegisterNewBook) {
        apply(command.apply { NewBookRegistered(bookId = UUID.randomUUID(), isbn = isbn, owner = owner) })
    }

    @CommandHandler
    fun borrowBook(borrowBook: BorrowBook) =
        if (bookState == BookState.AVAILABLE)
            apply(borrowBook.apply { BookBorrowed(renter) })
        else
            throw BookAlreadyBorrowed()


    @CommandHandler
    fun returnBook(returnBook: ReturnBook) =
        when {
            bookState == BookState.AVAILABLE -> throw BookAlreadyAvailable()
            borrower != returnBook.renter -> throw OnlyRenterMayReturnBook()
            else -> apply(BookReturned())
        }

    @EventSourcingHandler
    fun on(evt: NewBookRegistered) {
        this.bookId = evt.bookId
    }

    @EventSourcingHandler
    fun on(evt: BookBorrowed) {
        bookState = BookState.BORROWED
        borrower = evt.renter
    }

    @EventSourcingHandler
    fun on(evt: BookReturned) {
        bookState = BookState.AVAILABLE
        borrower = null
    }
}


data class RegisterNewBook(val isbn: String, val owner: Party)
data class BorrowBook(val renter: Party)
data class ReturnBook(val renter: Party)

class NewBookRegistered(val bookId: UUID, val isbn: String, val owner: Party)
data class BookBorrowed(val renter: Party)
class BookReturned()

enum class BookState {
    AVAILABLE,
    BORROWED
}

class BookAlreadyBorrowed() : Exception()
class BookAlreadyAvailable() : Exception()
class OnlyRenterMayReturnBook() : Exception()
