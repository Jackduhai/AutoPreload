package com.jack.library

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit

class Preload {
    companion object{
        fun init(application: Application){
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresStorageNotLow(true)
                .setRequiresDeviceIdle(false)
                .build()
            val initWorkRequest = OneTimeWorkRequestBuilder<AppInitWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(application).enqueue(initWorkRequest)
        }
    }
}