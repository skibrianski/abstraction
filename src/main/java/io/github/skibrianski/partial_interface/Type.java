package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
public @interface Type {
    Class<?> byClass() default NotSpecified.class;
    String value() default "";

    interface NotSpecified { }

    class Util {
        public static String stringify(Type type) {
            if (NotSpecified.class.equals(type.byClass())) {
                return type.value();
            } else {
                return type.byClass().getSimpleName();
            }
        }
    }
}

