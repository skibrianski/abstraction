package io.github.skibrianski.abstraction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(RequiresTypeParameters.class)
@Target({ElementType.TYPE})
public @interface RequiresTypeParameter {
    String value();
    String[] upperBound() default {};
    String[] lowerBound() default {};
}

