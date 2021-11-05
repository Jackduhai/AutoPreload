package com.jack.autopreload

import com.jack.annotation.AutoPreload
import com.jack.annotation.LoadMethod

@AutoPreload(process = "main")
class Load3 {

    @LoadMethod
    fun Load(){
        println("==========load3========")
    }


}