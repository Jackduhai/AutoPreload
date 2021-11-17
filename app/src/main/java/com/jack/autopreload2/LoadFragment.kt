package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.CleanMethod
import com.jack.annotation.LoadMethod
import com.jack.annotation.ThreadMode

@AutoPreload(target = "com.jack.autopreload.SettingsFragment",process = ":p2")
class LoadFragment {

    @LoadMethod(threadMode = ThreadMode.BACKGROUND)
    fun loadFragmentPre(){
        println("${this}==========LoadFragment============${Thread.currentThread().name}")
    }

    @CleanMethod(threadMode = ThreadMode.MAIN)
    fun cleanFragment(){
        println("${this}========cleanLoadFragment=========${Thread.currentThread().name}")
    }

}