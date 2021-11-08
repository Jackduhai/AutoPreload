package com.jack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明加载需要调用的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface LoadMethod {
    ThreadMode threadMode() default ThreadMode.BACKGROUND;
}
