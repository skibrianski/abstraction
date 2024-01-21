package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Target({ElementType.TYPE})
public @interface Type {

    Class<?> value();
    String parameterName() default "";

    interface TypeParameter { }

    class Util {
        public static String stringify(Type type) {
            if (TypeParameter.class.isAssignableFrom(type.value())) {
                return type.parameterName();
            } else {
                return type.value().getSimpleName();
            }
        }
    }
}

