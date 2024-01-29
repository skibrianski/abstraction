package io.github.skibrianski.abstraction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(HasTypeParameters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface HasTypeParameter {
    String name();
    Class<?> ofClass() default None.class;
    String ofString() default "";
    class None { }
}

