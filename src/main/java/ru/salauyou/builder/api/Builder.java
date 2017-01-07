package ru.salauyou.builder.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Builder {

  Class<?>[] appliedTo() default {};

  Class<?>[] dependsOn() default {};

  boolean stopOnException() default false;

}
