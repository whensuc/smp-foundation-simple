package cn.com.dyg.work.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
* 标题: 建表注解
* 作者 : wzy
* 日期 : 2017-9-12
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention (RetentionPolicy.RUNTIME)
public @interface ZTable {

	String tableName() default "";

}
