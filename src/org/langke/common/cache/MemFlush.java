package org.langke.common.cache;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title: memFlush<br>
 * Description: <br>
 * Copyright: Copyright (c) 2011	<br>
 * Create DateTime: 2011-10-28  <br>
 * @author langke
 */
/* 
* 元注解@Target,@Retention,@Documented,@Inherited 
* 
*      @Target 表示该注解用于什么地方，可能的 ElemenetType 参数包括： 
*          ElemenetType.CONSTRUCTOR 构造器声明 
*          ElemenetType.FIELD 域声明（包括 enum 实例） 
*          ElemenetType.LOCAL_VARIABLE 局部变量声明 
*          ElemenetType.METHOD 方法声明 
*          ElemenetType.PACKAGE 包声明 
*          ElemenetType.PARAMETER 参数声明 
*          ElemenetType.TYPE 类，接口（包括注解类型）或enum声明 
*          
*      @Retention 表示在什么级别保存该注解信息。可选的 RetentionPolicy 参数包括： 
*          RetentionPolicy.SOURCE 注解将被编译器丢弃 
*          RetentionPolicy.CLASS 注解在class文件中可用，但会被VM丢弃 
*          RetentionPolicy.RUNTIME VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息。 
*          
*      @Documented 将此注解包含在 javadoc 中 
*      
*      @Inherited 允许子类继承父类中的注解 
*    
*/   
@Target(ElementType.METHOD)    
@Retention(RetentionPolicy.RUNTIME)    
@Documented   
@Inherited
public @interface MemFlush {
	
	/*
	* 指定要刷新的方法名,多个方法用“，”分隔 <br>
	* 不指定，默认刷新对应实体类中所有带@MemCache的所有方法
	*  格式： 
	*/
	public String key() default "";
	
}