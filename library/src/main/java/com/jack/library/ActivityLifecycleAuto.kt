package com.jack.library

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * 监听activity生命周期 方便配置activity启动预加载和销毁时自动释放加载资源(自动与对应的activity或者fragment进行生命周期绑定)
 */
class ActivityLifecycleAuto : Application.ActivityLifecycleCallbacks {

    private val fragmentLifecycleAndroidX = FragmentLifecycleAndroidX()


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        println("=========onActivityCreated=======${activity}")
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleAndroidX,true)
        }
        LoadCenter.loadActivityPre(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        println("=========onActivityDestroyed==========${activity}")
        LoadCenter.loadActivityDestroy(activity)
    }
}