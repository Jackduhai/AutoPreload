package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.CleanMethod
import com.jack.annotation.LoadMethod
import com.jack.annotation.ThreadMode

@AutoPreload(process = ":p2",target = "com.jack.autopreload.MainActivity2")
object LoadNews {

    @LoadMethod
    fun loadMyMessagePre(){
        println("${this}==========LoadNews============${Thread.currentThread().name}")
    }

    @CleanMethod(threadMode = ThreadMode.MAIN)
    fun cleanTwo(){
        println("${this}========cleanTwo=========${Thread.currentThread().name}")
    }

}