package com.jack.library

import android.app.Activity
import android.content.Context
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase
import java.util.*

object LoadCenter {

    private var cls : Class<*>? = null
    private var _loadObj : Any? = null

    fun loadClass(appContext: Context){
        cls = Class.forName("${AUTO_CODE_PACKAGE}.${LOAD_CLASS}")
        _loadObj = cls?.getConstructor()?.newInstance()// as InvokeBase
//        obj.load()
        val load = cls?.getDeclaredMethod("load",Context::class.java)
        load?.isAccessible = true
        load?.invoke(_loadObj,appContext)
    }

    /**
     * activity初始化预加载
     */
    fun loadActivityPre(activity:Activity){
        _loadObj?.let {
            val loadActivity = cls?.getDeclaredMethod("loadActivity",Activity::class.java)
            loadActivity?.isAccessible = true
            loadActivity?.invoke(_loadObj,activity)
        }
    }

    /**
     * activity销毁时删除数据
     */
    fun loadActivityDestroy(activity: Activity){
        _loadObj?.let {
            val destroyActivity = cls?.getDeclaredMethod("destroyActivity",Activity::class.java)
            destroyActivity?.isAccessible = true
            destroyActivity?.invoke(_loadObj,activity)
        }
    }

    /**
     * fragment初始化预加载
     */
    fun loadFragmentPre(fragment:androidx.fragment.app.Fragment){
        _loadObj?.let {
            val loadFragment = cls?.getDeclaredMethod("loadFragment",androidx.fragment.app.Fragment::class.java)
            loadFragment?.isAccessible = true
            loadFragment?.invoke(_loadObj,fragment)
        }
    }

    fun loadFragmentDestroy(fragment:androidx.fragment.app.Fragment){
        _loadObj?.let {
            val destroyFragment = cls?.getDeclaredMethod("destroyFragment",androidx.fragment.app.Fragment::class.java)
            destroyFragment?.isAccessible = true
            destroyFragment?.invoke(_loadObj,fragment)
        }
    }

}