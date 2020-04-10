package cn.com.dyg.work.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
* 标题: 限制接口请求频率注解
* 作者 : wzy
* 日期 : 2017-9-12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestLimit {
	int count() default Integer.MAX_VALUE;
	long time() default 60000;
}
