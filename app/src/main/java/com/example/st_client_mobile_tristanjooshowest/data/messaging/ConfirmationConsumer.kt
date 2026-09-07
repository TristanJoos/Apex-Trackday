package com.example.st_client_mobile_tristanjooshowest.data.messaging

interface ConfirmationConsumer {
    fun startConsuming()

    fun stopConsuming()

    var onMessageReceived: (String) -> Unit
}