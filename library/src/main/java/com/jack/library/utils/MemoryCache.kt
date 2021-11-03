package com.jack.library.utils

import androidx.collection.LruCache

/**
 * 系统内存缓存
 */
object MemoryCache {

    private val lruCache by lazy {
        val maxMemory = Runtime.getRuntime().totalMemory().toInt()
        val cacheSize = maxMemory / 8
        object : LruCache<String,Triple<String,Long,Long>>(cacheSize){
            override fun sizeOf(key: String, value: Triple<String,Long,Long>): Int {
                return value.first.toByteArray().size
            }
        }
    }

    /**
     * key
     * value
     * effectTime 缓存有效时间 毫秒 默认有效5分钟
     */
    fun put(key:String,value:String,effectTime:Long = (1 * 1000 * 60 * 5)){
        lruCache.put(key, Triple(value,System.currentTimeMillis(),effectTime))
    }

    fun get(key:String):String?{
        val value = lruCache[key] ?: return null
        if(value.third == -1L){
            //永不过期
            return value.first
        }
        //看是否过期 过期就返回空并删除缓存
        if((System.currentTimeMillis() - value.second) > value.third){
            remove(key)
            return null
        }
        return value.first
    }

    fun remove(key: String){
        lruCache.remove(key)
    }

    fun clean(){
        lruCache.evictAll()
    }
}
