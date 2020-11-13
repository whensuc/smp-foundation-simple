package cn.com.dyg.work.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZColumn {
    String columnType() default "";
    String columnLength() default "";
    String defaultValue() default "DEFAULT NULL";
    String showName() default "";
    boolean isPrimary()default false;
    String oldColumnName() default "";
    boolean isAuto() default false;
}
