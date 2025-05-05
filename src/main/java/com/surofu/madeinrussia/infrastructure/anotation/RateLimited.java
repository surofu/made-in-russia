package com.surofu.madeinrussia.infrastructure.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    int capacity() default 100;
    int refillTokens() default 10;
    int refillMinutes() default 1;
    String type() default "IP"; // IP, USER, API_KEY
}
