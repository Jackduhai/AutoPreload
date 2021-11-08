package com.jack.library

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jack.library.utils.ProcessUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Preload {

    /**
     * true支持多进程 初始化时会根据配置 在限制的进程中初始化   false不支持多进程 初始化时不区分进程 全部初始化
     */
    private var mutiprocess : Boolean = false

    fun init(application: Application){
        GlobalScope.launch(Dispatchers.IO){
            println("===Preload===init======${ProcessUtil.getCurrentProcessName(application)}")
            AppInitWorker(application).doWork()
        }
    }

    fun setMultiProcess(support:Boolean){
        mutiprocess = support
    }

    fun isMultiProcess():Boolean{
        return mutiprocess
    }

}