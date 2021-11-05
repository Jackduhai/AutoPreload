package com.jack.autopreload

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.WorkManager
import com.jack.library.Preload

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        println("========AppApplication=========${this}")
        Preload.init(this)
    }
}