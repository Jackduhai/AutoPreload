package com.jack.autopreload

import android.app.Application
import com.jack.annotation.*

@AutoPreload(process = "main")
object Load3 {

    @ApplicationInject
    public var application : Application?=null

    @LoadMethod(threadMode = ThreadMode.MAIN)
    fun Load(){
        println("${application}==========load3========${Thread.currentThread().name}")
    }

    @CleanMethod
    fun cleanLoad3(){

    }


}