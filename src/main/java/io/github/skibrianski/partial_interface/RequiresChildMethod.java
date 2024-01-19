package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repeatable(RequiresChildMethods.class)
public @interface RequiresChildMethod {
    // TODO: support parameterized types
    Class<?> returnType();
    // TODO: support parameterized types
    Class<?>[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

    class Util {
        public static String stringify(RequiresChildMethod requiresChildMethod) {
            List<String> argumentTypeList = Arrays.stream(requiresChildMethod.argumentTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.toList());
            String argumentString = String.join(", ", argumentTypeList);
            String staticPrefix = requiresChildMethod.isStatic() ? "static " : "";
            return String.format(
                    "%s%s %s(%s)",
                    staticPrefix,
                    requiresChildMethod.returnType().getSimpleName(),
                    requiresChildMethod.methodName(),
                    argumentString
            );
        }
    }
}

