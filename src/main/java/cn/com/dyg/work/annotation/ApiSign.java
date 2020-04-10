package cn.com.dyg.work.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD
})
public @interface  ApiSign {
	 boolean enableSign() default true;
	 String signKey() default "636e2e636f6d2e64";
	 boolean hasTimestamp() default true;
	 String serviceName() default "";
}
