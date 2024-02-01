package io.github.skibrianski.abstraction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(RequiresTypeParameters.class)
@Target({ElementType.TYPE})
public @interface RequiresTypeParameter {
    // TODO: consider accepting this all as a String value and just parsing it out instead?
    String value();
    String[] superOf() default {};
    String[] extending() default {};
}

