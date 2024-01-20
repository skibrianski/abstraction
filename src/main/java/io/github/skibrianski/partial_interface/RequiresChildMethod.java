package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repeatable(RequiresChildMethods.class)
public @interface RequiresChildMethod {
    // TODO: should return type default to Void and argumentTypes default to {} ?
    Type returnType();
    Type[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

    @interface Type {
        Class<?> value();
        int parameterNumber() default -1; // TODO: a little weird that these start at 0. names would be better anyhow
    }

    interface TypeParameter { }

    class Util {
        public static String stringify(RequiresChildMethod requiresChildMethod) {
            List<String> argumentTypeList = Arrays.stream(requiresChildMethod.argumentTypes())
                    .map(Util::stringify)
                    .collect(Collectors.toList());
            String argumentString = String.join(", ", argumentTypeList);
            String staticPrefix = requiresChildMethod.isStatic() ? "static " : "";
            return String.format(
                    "%s%s %s(%s)",
                    staticPrefix,
                    stringify(requiresChildMethod.returnType()),
                    requiresChildMethod.methodName(),
                    argumentString
            );
        }

        public static String stringify(Type type) {
            if (TypeParameter.class.isAssignableFrom(type.value())) {
                int parameterNumber = type.parameterNumber();
                return "P" + parameterNumber;
            } else {
                return type.value().getSimpleName();
            }
        }
    }
}

