package com.ksidelta.libruch.modules.product


import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class ProductAggregate() {

    @AggregateIdentifier
    lateinit var bookId: UUID

    @CommandHandler
    constructor(command: RegisterNewProduct) : this() {
        apply(command.run { NewProductRegistered(bookId = UUID.randomUUID(), isbn = isbn, title = title, author = author) })
    }

    @EventSourcingHandler
    fun on(evt: NewProductRegistered) {
        this.bookId = evt.bookId
    }
}


data class RegisterNewProduct(val isbn: String, val title: String, val author: String)

data class NewProductRegistered(val bookId: UUID, val isbn: String, val title: String, val author: String)


class ProductAlreadyExist() : Exception("Product Already Exist")
