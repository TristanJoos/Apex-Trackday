package com.example.st_client_mobile_tristanjooshowest.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker (
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val eventName = inputData.getString("EVENT_NAME") ?: return Result.failure()
        NotificationHelper.showRaceNotification(applicationContext, eventName)
        return Result.success()
    }
}