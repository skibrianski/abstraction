package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repeatable(RequiresChildMethods.class)
@Target({ElementType.TYPE})
public @interface RequiresChildMethod {
    // TODO: should return type default to void and argumentTypes default to {} ?
    Type returnType();
    Type[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

    @interface Type {
        Class<?> value();
        String parameterName() default "";
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
                return type.parameterName();
            } else {
                return type.value().getSimpleName();
            }
        }
    }
}

