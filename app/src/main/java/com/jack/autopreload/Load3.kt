package com.jack.autopreload

import com.jack.annotation.AutoPreload
import com.jack.annotation.LoadMethod
import com.jack.annotation.ThreadMode

@AutoPreload(process = "main")
class Load3 {

    @LoadMethod(threadMode = ThreadMode.MAIN)
    fun Load(){
        println("==========load3========${Thread.currentThread().name}")
    }


}