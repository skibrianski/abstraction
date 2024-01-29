package io.github.skibrianski.abstraction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
public @interface RequiresTypeParameters {
    RequiresTypeParameter[] value();
}

