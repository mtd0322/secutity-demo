package org.secutity.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DebugLogger {
    String detail() default "无";
}
