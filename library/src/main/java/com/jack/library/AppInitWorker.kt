package com.jack.library

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.work.*

class AppInitWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            println("===========doWork============")
            val outputData = workDataOf(Pair("",""))
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

}