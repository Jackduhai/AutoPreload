package com.jack.library.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context

import android.os.Build
import android.os.Process

import android.text.TextUtils
import java.lang.reflect.Method


class ProcessUtil {
    companion object{
        private var currentProcessName: String? = null

        /**
         * @return 当前进程名
         */
        fun getCurrentProcessName(context: Context?): String? {
            if (!TextUtils.isEmpty(currentProcessName)) {
                return currentProcessName
            }

            //1)通过Application的API获取当前进程名
            currentProcessName = getCurrentProcessNameByApplication()
            if (!TextUtils.isEmpty(currentProcessName)) {
                return currentProcessName
            }

            //2)通过反射ActivityThread获取当前进程名
            currentProcessName = getCurrentProcessNameByActivityThread()
            if (!TextUtils.isEmpty(currentProcessName)) {
                return currentProcessName
            }

            //3)通过ActivityManager获取当前进程名
            currentProcessName = getCurrentProcessNameByActivityManager(context)
            return currentProcessName
        }


        /**
         * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
         */
        fun getCurrentProcessNameByApplication(): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Application.getProcessName()
            } else null
        }

        /**
         * 通过反射ActivityThread获取进程名，避免了ipc
         */
        fun getCurrentProcessNameByActivityThread(): String? {
            var processName: String? = null
            try {
                val declaredMethod: Method = Class.forName(
                    "android.app.ActivityThread", false,
                    Application::class.java.getClassLoader()
                )
                    .getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
                declaredMethod.setAccessible(true)
                val invoke: Any = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
                if (invoke is String) {
                    processName = invoke
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return processName
        }

        /**
         * 通过ActivityManager 获取进程名，需要IPC通信
         */
        fun getCurrentProcessNameByActivityManager(context: Context?): String? {
            if (context == null) {
                return null
            }
            val pid: Int = Process.myPid()
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (am != null) {
                val runningAppList = am.runningAppProcesses
                if (runningAppList != null) {
                    for (processInfo in runningAppList) {
                        if (processInfo.pid == pid) {
                            return processInfo.processName
                        }
                    }
                }
            }
            return null
        }
    }
}