package cn.com.dyg.work.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD
})
public @interface TokenCheck {
	boolean enableCheck() default true;
	String serviceName() default "redisTokenImpl";
}
