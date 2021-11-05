package com.jack.library

import android.content.Context
import android.util.Log
import androidx.work.*

class AppInitWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            Log.d("AppInitWorker","===========doWork============")
            LoadCenter.loadClass(applicationContext)
            val outputData = workDataOf(Pair("",""))
            Result.success(outputData)
        } catch (e: Exception) {
            e.message?.let { Log.e("AppInitWorker", it) }
            Result.failure()
        }
    }


}