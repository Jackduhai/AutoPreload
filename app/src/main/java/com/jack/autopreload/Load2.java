package com.jack.autopreload;

import com.jack.annotation.AutoPreload;
import com.jack.annotation.LoadMethod;

@AutoPreload
public class Load2 {
    @LoadMethod
    public void loadMyMessagePre2(){
        System.out.println("==========loadMyMessagePre2============");
    }
}
