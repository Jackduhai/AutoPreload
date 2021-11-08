package com.jack.autopreload;

import com.jack.annotation.AutoPreload;
import com.jack.annotation.LoadMethod;

@AutoPreload(process = ":p2")
public class Load2 {
    @LoadMethod
    public void loadMyMessagePre2(){
        System.out.println("==========loadMyMessagePre2============");
    }
}
