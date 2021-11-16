package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.CleanMethod
import com.jack.annotation.LoadMethod
import com.jack.annotation.ThreadMode

@AutoPreload(process = ":p2",target = "com.jack.autopreload.SettingsFragment")
class LoadFragment {

    @LoadMethod
    fun loadFragmentPre(){
        println("${this}==========LoadFragment============${Thread.currentThread().name}")
    }

    @CleanMethod(threadMode = ThreadMode.MAIN)
    private fun cleanFragment(){
        println("${this}========cleanLoadFragment=========${Thread.currentThread().name}")
    }

}