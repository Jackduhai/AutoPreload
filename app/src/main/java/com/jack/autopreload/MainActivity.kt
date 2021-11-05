package com.jack.autopreload

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.NetworkType
import com.jack.library.Preload

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Preload.prebuilder.setRequiredNetworkType(NetworkType.CONNECTED)
        startActivity(Intent(this,MainActivity2::class.java))

    }
}