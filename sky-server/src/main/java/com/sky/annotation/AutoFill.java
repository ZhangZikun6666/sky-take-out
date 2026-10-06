package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 用于标识公共字段需要进行自动填充的处理
 * */
@Target(ElementType.METHOD) //标识此注解只能加在方法上面
@Retention(RetentionPolicy.RUNTIME) //固定写法
public @interface AutoFill {
    //指定数据库操作类型 UPDATE INSERT
    OperationType value();

}
