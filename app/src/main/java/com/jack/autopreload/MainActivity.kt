package com.jack.autopreload

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.NetworkType
import com.jack.library.Preload

//可配置在application初始化时就调用还是某个activity启动时调用
//在执行完成时给个回调，需要解偶 调用的地方和监听回调的地方要分开
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Preload.prebuilder.setRequiredNetworkType(NetworkType.CONNECTED)
//        startActivity(Intent(this,MainActivity2::class.java))
        println("========mainactivity===========${intent.getStringExtra("autoTest")}")
    }
}