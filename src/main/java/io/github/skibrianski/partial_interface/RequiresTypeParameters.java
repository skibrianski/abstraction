package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
public @interface RequiresTypeParameters {
    RequiresTypeParameter[] value();
}

