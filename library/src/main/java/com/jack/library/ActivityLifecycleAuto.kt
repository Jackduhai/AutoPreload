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
        println("=========onActivityCreated====${savedInstanceState}======${activity}")
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleAndroidX,true)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        println("=========onActivityStarted==========${activity}")
    }

    override fun onActivityResumed(activity: Activity) {
        println("=========onActivityResumed==========${activity}")
    }

    override fun onActivityPaused(activity: Activity) {
        println("=========onActivityPaused==========${activity}")
    }

    override fun onActivityStopped(activity: Activity) {
        println("=========onActivityStopped==========${activity}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        println("=========onActivitySaveInstanceState==========${activity}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        println("=========onActivityDestroyed==========${activity}")
    }
}