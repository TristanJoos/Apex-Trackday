package com.example.st_client_mobile_tristanjooshowest.data.messaging

import android.util.Log
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class RabbitMQPublisher(
    private val host: String,
    private val queueName: String
) : MessagePublisher {
    private val factory = ConnectionFactory().apply {
        try {
            if (host.isNotBlank() && (host.startsWith("amqp://") || host.startsWith("amqps://"))) {
                setUri(host)
            } else {
                Log.e("RabbitMQPublisher", "Invalid RabbitMQ URI: '$host'. It must start with amqp:// or amqps://")
                setUri("amqp://guest:guest@10.0.2.2:40300")
            }
        } catch (e: Exception) {
            Log.e("RabbitMQPublisher", "Failed to set RabbitMQ URI: $host", e)
        }
    }
    private var connection: Connection? = null

    @Synchronized
    private fun getConnection(): Connection? {
        if (connection == null || connection?.isOpen == false) {
            try {
                connection = factory.newConnection()
            } catch (e: Exception) {
                Log.e("RabbitMQPublisher", "Error establishing RabbitMQ connection: ${e.message}")
            }
        }
        return connection
    }

    override fun publishMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = getConnection() ?: return@launch
                conn.createChannel().use { channel ->
                    channel.queueDeclare(queueName, true, false, false, null)
                    channel.basicPublish(
                        "",
                        queueName,
                        null,
                        message.toByteArray(StandardCharsets.UTF_8)
                    )
                    Log.d("RabbitMQPublisher", "Sent message to $queueName: $message")
                }
            } catch (e: Exception) {
                Log.e("RabbitMQPublisher", "Error sending message to RabbitMQ: ${e.message}")
            }
        }
    }
}