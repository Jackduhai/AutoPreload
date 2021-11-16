package com.jack.autopreload;

import com.jack.annotation.AutoPreload;
import com.jack.annotation.CleanMethod;
import com.jack.annotation.LoadMethod;
import com.jack.annotation.ThreadMode;

@AutoPreload(target = "com.jack.autopreload.MainActivity2",process = ":p2")
public class Load2 {
    @LoadMethod(threadMode = ThreadMode.MAIN)
    public void loadMyMessagePre2(){
        System.out.println(this+"==========loadMyMessagePre2============"+Thread.currentThread().getName());
    }

    @CleanMethod(threadMode = ThreadMode.MAIN)
    public void clean(){
        System.out.println(this+"===========clean=============="+Thread.currentThread().getName());
    }


}
