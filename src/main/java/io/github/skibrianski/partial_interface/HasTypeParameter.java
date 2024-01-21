package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(HasTypeParameters.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasTypeParameter {
    String name();
    Class<?> value();
}

