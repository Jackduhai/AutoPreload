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
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        println("==========onFragmentDestroyed============${f}")
        LoadCenter.loadFragmentDestroy(f)
    }

}