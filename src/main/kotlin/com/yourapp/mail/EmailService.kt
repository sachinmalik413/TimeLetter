package com.yourapp.mail

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import java.io.IOException

class EmailService(private val apiKey: String) {
    fun send(to: String, subject: String, content: String) {
        val from = Email("noreply@yourdomain.com")
        val toEmail = Email(to)
        val message = Mail(from, subject, toEmail, Content("text/plain", content))
        val sg = SendGrid(apiKey)
        val request = Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = message.build()
            sg.api(request)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}
