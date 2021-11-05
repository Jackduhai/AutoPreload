package com.jack.library

import android.content.Context
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase

object LoadCenter {

    fun loadClass(appContext: Context){
        val cls = Class.forName("${AUTO_CODE_PACKAGE}.${LOAD_CLASS}")
        val obj = cls.getConstructor().newInstance()// as InvokeBase
//        obj.load()
        val load = cls.getDeclaredMethod("load",Context::class.java)
        load.isAccessible = true
        load.invoke(obj,appContext)
    }

}