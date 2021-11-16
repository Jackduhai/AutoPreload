package com.jack.autopreload2

import com.jack.annotation.AutoPreload
import com.jack.annotation.CleanMethod
import com.jack.annotation.LoadMethod
import com.jack.annotation.ThreadMode

@AutoPreload(target = "com.jack.autopreload.SettingsFragment",process = ":p2")
class LoadFragment {

    @LoadMethod(threadMode = ThreadMode.BACKGROUND) //如果有声明生命周期的target且是非单例对象则 此方法的threadMode不生效 都为ui线程调用
    fun loadFragmentPre(){
        println("${this}==========LoadFragment============${Thread.currentThread().name}")
    }

    @CleanMethod(threadMode = ThreadMode.BACKGROUND) //如果有声明生命周期的target且是非单例对象则 此方法的threadMode不生效 都为ui线程调用
    fun cleanFragment(){
        println("${this}========cleanLoadFragment=========${Thread.currentThread().name}")
    }

}