package com.jack.library

import android.app.Activity
import android.content.Context
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase
import java.util.*

object LoadCenter {

    private var _loadObj : Any? = null

    fun loadClass(appContext: Context){
        val cls = Class.forName("${AUTO_CODE_PACKAGE}.${LOAD_CLASS}")
        _loadObj = cls.getConstructor().newInstance()// as InvokeBase
//        obj.load()
        val load = cls.getDeclaredMethod("load",Context::class.java)
        load.isAccessible = true
        load.invoke(_loadObj,appContext)
    }

    /**
     * activity初始化预加载
     */
    fun loadActivityPre(activity:Activity){

    }

    /**
     * fragment初始化预加载
     */
    fun loadFragmentPre(fragment:androidx.fragment.app.Fragment){

    }



}