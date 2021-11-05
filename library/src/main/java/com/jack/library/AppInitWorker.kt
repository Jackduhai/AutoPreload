package com.jack.library

import android.content.Context
import android.util.Log

class AppInitWorker(appContext: Context) {

    private var appContext : Context = appContext

     fun doWork(){
       try {
            Log.d("AppInitWorker","===========doWork============")
            LoadCenter.loadClass(appContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}