package com.jack.autopreload

import com.jack.annotation.AutoPreload
import com.jack.annotation.LoadMethod

@AutoPreload
class LoadNews {

    @LoadMethod
    fun loadMyMessagePre(){
        println("==========loadMyMessagePre============")
    }


}