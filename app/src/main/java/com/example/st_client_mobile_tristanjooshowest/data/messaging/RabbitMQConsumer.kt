package com.example.st_client_mobile_tristanjooshowest.data.messaging

import android.util.Log
import com.rabbitmq.client.*
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets

class RabbitMQConsumer(
    private val host: String,
    private val queueName: String
) : ConfirmationConsumer {
    private val factory = ConnectionFactory().apply {
        try {
            if (host.isNotBlank() && (host.startsWith("amqp://") || host.startsWith("amqps://"))) {
                setUri(host)
            } else {
                Log.e("RabbitMQConsumer", "Invalid RabbitMQ URI: '$host'. It must start with amqp:// or amqps://")
                setUri("amqp://guest:guest@10.0.2.2:40300")
            }
        } catch (e: Exception) {
            Log.e("RabbitMQConsumer", "Failed to set RabbitMQ URI: $host", e)
        }
    }
    private var consumerJob: Job? = null
    private var connection: Connection? = null
    private var channel: Channel? = null

    override var onMessageReceived: (String) -> Unit = { Log.w("Messagebroker", "onMessageReceived not set") }

    override fun startConsuming() {
        consumerJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                connection = factory.newConnection()
                channel = connection?.createChannel()

                channel?.queueDeclare(queueName, true, false, false, null)

                val deliverCallback = DeliverCallback { _, delivery ->
                    val message = String(delivery.body, StandardCharsets.UTF_8)
                    onMessageReceived(message)
                }

                channel?.basicConsume(queueName, true, deliverCallback, CancelCallback {
                    Log.w("Messagebroker", "Consumer cancelled")
                })

                suspendCancellableCoroutine<Unit> { continuation ->
                    continuation.invokeOnCancellation {
                        stopConsuming()
                    }
                }

            } catch (e: Exception) {
                Log.e("Messagebroker", "Error subscribing to Messagebroker! - ${e.message}")
            }
        }
    }

    override fun stopConsuming() {
        try {
            channel?.close()
            connection?.close()
        } catch (e: Exception) {
            Log.e("Messagebroker", "Error closing RabbitMQ connection: ${e.message}")
        } finally {
            consumerJob?.cancel()
            channel = null
            connection = null
        }
    }
}