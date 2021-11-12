package com.jack.autopreload;

import com.jack.annotation.AutoPreload;
import com.jack.annotation.LoadMethod;
import com.jack.annotation.ThreadMode;

@AutoPreload(process = ":p2",target = "com.jack.autopreload.MainActivity")
public class Load2 {
    @LoadMethod(threadMode = ThreadMode.MAIN)
    public void loadMyMessagePre2(){
        System.out.println("==========loadMyMessagePre2============"+Thread.currentThread().getName());
    }
}
