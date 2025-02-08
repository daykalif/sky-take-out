package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 第一步：自定义注解 AutoFill
 *
 * 用于标识某个方法, 需要进行功能字段自动填充处理
 * 创建时，“new Java Class” 的时候需要选择 “Annotation”
 */
@Target(ElementType.METHOD)    // 表明该注解用于方法上
@Retention(RetentionPolicy.RUNTIME)    // 表明该注解在运行时存在
public @interface AutoFill {
	/*
	 * 指定一个属性，指定当前数据库操作的类型，枚举类型，当前已定义在 “sky-common/src/main/java/com/sky/enumeration/OperationType.java” 中
	 *
	 * 数据库操作类型：UPDATE INSERT
	 * （没有设置 PUT 和 DELETE 是因为该注解不需要在这两种数据库操作类型时执行）
	 */
	OperationType value();
}
