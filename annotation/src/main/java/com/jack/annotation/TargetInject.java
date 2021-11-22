package com.jack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明自动注入绑定的生命周期对象
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface TargetInject {

}
