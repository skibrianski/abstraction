package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(RequiresTypeParameters.class)
@Target({ElementType.TYPE})
public @interface RequiresTypeParameter {
    // TODO: how to express type bounds? just string?
    // TODO: can't upperBound and lowerBound both be arrays?
    String value();
    String upperBound() default "";
    String lowerBound() default "";
}

