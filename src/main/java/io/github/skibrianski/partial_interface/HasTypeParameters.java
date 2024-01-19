package io.github.skibrianski.partial_interface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HasTypeParameters {
    Class<?>[] value();
}

