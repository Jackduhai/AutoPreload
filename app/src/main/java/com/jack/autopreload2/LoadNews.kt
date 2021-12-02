package com.jack.autopreload2

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.jack.annotation.*

@SuppressLint("StaticFieldLeak")
@AutoPreload(process = ":p2",target = "com.jack.autopreload.MainActivity2")
object LoadNews {

    @TargetInject
    public var context: AppCompatActivity? = null

    @LoadMethod
    fun loadMyMessagePre(){
        println("${this}==========LoadNews============${Thread.currentThread().name}  context:${context}")
        context?.intent?.putExtra("aop","from LoadNews")
    }

//    @CleanMethod(threadMode = ThreadMode.MAIN)
//    fun cleanTwo(){
//        println("${this}========cleanTwo=========${Thread.currentThread().name}  context:${context}")
//    }

}