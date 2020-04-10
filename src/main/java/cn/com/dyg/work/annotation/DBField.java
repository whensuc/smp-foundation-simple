package cn.com.dyg.work.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
* 标题: 实体类属性注解
* 描述: 属性使用该注解后，在实体类保存和修改中会忽略注解的字段
* 作者 : wzy
* 日期 : 2017-9-12
 */
@Target(ElementType.FIELD)
@Retention (RetentionPolicy.RUNTIME)
public @interface DBField {
	boolean isIgnore() default false;
	boolean isWhere() default false;
	boolean isPrimary() default false;
}
