package com.jack.library

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jack.library.utils.ProcessUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Preload {

    private var mutiprocess : Boolean = true

    fun init(application: Application){
        GlobalScope.launch(Dispatchers.IO){
            println("===Preload===init======${ProcessUtil.getCurrentProcessName(application)}")
            AppInitWorker(application).doWork()
        }
    }

    fun setMultiProcess(support:Boolean){
        mutiprocess = support
    }

}