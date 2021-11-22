package com.jack.autopreload2

import androidx.fragment.app.Fragment
import com.jack.annotation.*

@AutoPreload(target = "com.jack.autopreload.SettingsFragment",process = ":p2")
object LoadFragment {

    @TargetInject
    public var fragment : Fragment? = null

    @LoadMethod(threadMode = ThreadMode.BACKGROUND)
    fun loadFragmentPre(){
        println("${this}==========LoadFragment============${Thread.currentThread().name}")
    }

    @CleanMethod(threadMode = ThreadMode.MAIN)
    fun cleanFragment(){
        println("${this}========cleanLoadFragment=========${Thread.currentThread().name}")
    }

}