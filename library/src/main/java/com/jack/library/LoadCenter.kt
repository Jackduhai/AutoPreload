package com.jack.library

import android.content.Context
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase

object LoadCenter {

    fun loadClass(appContext: Context){
        val obj = Class.forName("${AUTO_CODE_PACKAGE}.${LOAD_CLASS}").getConstructor().newInstance() as InvokeBase
        obj.load()
    }

}