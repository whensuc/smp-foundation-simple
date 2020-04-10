package cn.com.dyg.work.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
* 标题: 防止重复提交注解
* 作者 : wzy
* 日期 : 2017-9-12
 */
@Target(ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
public @interface SessionToken {
	boolean save() default false ;
    boolean remove() default false;
}
