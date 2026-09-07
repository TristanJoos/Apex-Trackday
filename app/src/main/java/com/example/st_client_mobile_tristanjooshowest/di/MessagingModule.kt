package com.example.st_client_mobile_tristanjooshowest.di

import com.example.st_client_mobile_tristanjooshowest.BuildConfig
import com.example.st_client_mobile_tristanjooshowest.data.messaging.ConfirmationConsumer
import com.example.st_client_mobile_tristanjooshowest.data.messaging.MessagePublisher
import com.example.st_client_mobile_tristanjooshowest.data.messaging.RabbitMQConsumer
import com.example.st_client_mobile_tristanjooshowest.data.messaging.RabbitMQPublisher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MessagingModule {

    private fun getSafeUrl(url: String?): String {
        val defaultUrl = "amqp://guest:guest@10.0.2.2:40300"
        if (url.isNullOrBlank() || url == "null") return defaultUrl
        return if (url.startsWith("amqp://") || url.startsWith("amqps://")) {
            url
        } else {
            // If it's just a host, prepend amqp://
            "amqp://$url"
        }
    }

    @Provides
    @Singleton
    fun provideConsumeService(): ConfirmationConsumer {
        val url = getSafeUrl(BuildConfig.AMQPurl)
        return RabbitMQConsumer(url, "tickets-queue")
    }

    @Provides
    @Singleton
    fun providePublishService(): MessagePublisher {
        val url = getSafeUrl(BuildConfig.AMQPurl)
        val queue = BuildConfig.AMQPpublishtopic.takeUnless { it.isNullOrBlank() || it == "null" } ?: "orders-queue"
        return RabbitMQPublisher(url, queue)
    }
}