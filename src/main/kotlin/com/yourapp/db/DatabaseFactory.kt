package com.yourapp.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Database

object Messages : Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255)
    val message = text("message")
    val deliveryDate = date("delivery_date")
    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    fun init() {
        val dbUrl = System.getenv("DATABASE_URL")
        requireNotNull(dbUrl) { "DATABASE_URL is not set" }
        Database.connect(dbUrl, driver = "org.postgresql.Driver")
        transaction {
            SchemaUtils.create(Messages)
        }
    }
}
