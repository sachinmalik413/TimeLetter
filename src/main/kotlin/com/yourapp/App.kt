package com.yourapp

import com.yourapp.db.DatabaseFactory
import com.yourapp.repo.MessageRepository
import com.yourapp.mail.EmailService
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import java.time.LocalDate

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }

    DatabaseFactory.init()

    val repo = MessageRepository()
    val emailService = EmailService(System.getenv("SENDGRID_API_KEY"))

    routing {
        post("/schedule-message") {
            val req = call.receive<ScheduleMessageRequest>()
            repo.insert(req.email, req.message, req.date)
            call.respondText("Message Scheduled")
        }

        // Trigger this daily via Render Cron
        get("/send-todays-messages") {
            val today = LocalDate.now()
            val messages = repo.getMessagesForDate(today)
            messages.forEach {
                emailService.send(it.email, "Message from Your Past Self", it.message)
            }
            call.respondText("Sent ${messages.size} messages")
        }
    }
}

@Serializable
data class ScheduleMessageRequest(val email: String, val message: String, val date: String)
