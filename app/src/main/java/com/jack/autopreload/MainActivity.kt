package com.jack.autopreload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jack.library.Preload

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Preload.init(application)
    }
}