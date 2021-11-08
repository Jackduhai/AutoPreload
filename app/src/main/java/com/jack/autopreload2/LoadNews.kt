package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.LoadMethod

@AutoPreload(process = "main")
object LoadNews {

    @LoadMethod
    fun loadMyMessagePre(){
        println("==========loadMyMessagePre============${Thread.currentThread().name}")
    }



}