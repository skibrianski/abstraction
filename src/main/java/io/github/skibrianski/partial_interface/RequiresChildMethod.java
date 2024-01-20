package io.github.skibrianski.partial_interface;

import java.lang.annotation.Annotation;
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
    }

    interface Parameter { }
    interface FirstParameter extends Parameter { }
    interface SecondParameter extends Parameter { }


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
            if (RequiresChildMethod.Parameter.class.isAssignableFrom(type.value())) {
                if (type.value().equals(FirstParameter.class)) {
                    return "P1";
                }
                if (type.value().equals(SecondParameter.class)) {
                    return "P2";
                }
                throw new RuntimeException("unimplemented");
            } else {
                return type.value().getSimpleName();
            }
        }
    }
}

