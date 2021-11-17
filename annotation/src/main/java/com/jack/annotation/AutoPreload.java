package com.jack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明需要被预加载的类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AutoPreload {
    String process() default "main";//如果支持多进程 不配置就默认主进程才初始化
    String target() default  "application";//设置目标类加载时才进行预加载调用 主要是针对activity和fragment
}
