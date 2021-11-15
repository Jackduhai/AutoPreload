package com.jack.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class FragmentLifecycleAndroidX : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentPreCreated(fm, f, savedInstanceState)
        println("==========onFragmentPreCreated============${f}")
        LoadCenter.loadFragmentPre(f)
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        println("==========onFragmentCreated============${f}")
    }

}