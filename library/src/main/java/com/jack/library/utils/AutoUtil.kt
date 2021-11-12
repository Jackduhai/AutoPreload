package com.jack.library.utils

val isAndroidX = findClassByClassName("androidx.fragment.app.FragmentActivity")

private fun findClassByClassName(className: String): Boolean {
    val isAndroidX = try {
        Class.forName(className)
        true
    } catch (e: ClassNotFoundException) {
        false
    }
    return isAndroidX
}