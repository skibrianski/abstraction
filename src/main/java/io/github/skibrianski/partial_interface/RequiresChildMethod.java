package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repeatable(RequiresChildMethods.class)
public @interface RequiresChildMethod {
    // TODO: support parameterized types
    Type returnType();
    // TODO: support parameterized types
    Type[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

    @interface Type {
        Class<?> value();
        TypeType type() default TypeType.REGULAR;
    }

    interface FirstParameter { }

    enum TypeType {
        REGULAR,
        PARAMETERIZED;
    }

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
            switch(type.type()) {
                case REGULAR:
                    return type.value().getSimpleName();
                case PARAMETERIZED:
                    if (type.value().equals(FirstParameter.class)) {
                        return "P1";
                    }
            }
            throw new RuntimeException("unimplemented");
        }
    }
}

