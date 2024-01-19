package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.stream.Collectors;

// TODO: name is wrong. as this is repeatable it should be something method-related
@Repeatable(PartialInterfaces.class)
public @interface PartialInterface {
    // TODO: support parameterized types
    Class<?> returnType();
    // TODO: support varargs
    // TODO: support parameterized types
    Class<?>[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

    class Util {
        public static String stringify(PartialInterface partialInterface) {
            String argumentString = Arrays.stream(partialInterface.argumentTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
            String staticPrefix = partialInterface.isStatic() ? "static " : "";
            return String.format(
                    "%s%s %s(%s)",
                    staticPrefix,
                    partialInterface.returnType().getSimpleName(),
                    partialInterface.methodName(),
                    argumentString
            );
        }
    }
}

