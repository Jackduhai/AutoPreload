package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.LoadMethod

@AutoPreload
object LoadNews {

    @LoadMethod
    fun loadMyMessagePre(){
        println("==========loadMyMessagePre============")
    }


}