package com.yourapp.repo

import com.yourapp.db.Messages
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

data class ScheduledMessage(val email: String, val message: String)

class MessageRepository {
    fun insert(email: String, message: String, date: String) {
        transaction {
            Messages.insert {
                it[Messages.email] = email
                it[Messages.message] = message
                it[Messages.deliveryDate] = LocalDate.parse(date)
            }
        }
    }

    fun getMessagesForDate(date: LocalDate): List<ScheduledMessage> {
        return transaction {
            Messages.select { Messages.deliveryDate eq date }
                .map { ScheduledMessage(it[Messages.email], it[Messages.message]) }
        }
    }
}
