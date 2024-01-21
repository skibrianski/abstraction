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

    class Util {
        public static String stringify(RequiresChildMethod requiresChildMethod) {
            List<String> argumentTypeList = Arrays.stream(requiresChildMethod.argumentTypes())
                    .map(Type.Util::stringify)
                    .collect(Collectors.toList());
            String argumentString = String.join(", ", argumentTypeList);
            String staticPrefix = requiresChildMethod.isStatic() ? "static " : "";
            return String.format(
                    "%s%s %s(%s)",
                    staticPrefix,
                    Type.Util.stringify(requiresChildMethod.returnType()),
                    requiresChildMethod.methodName(),
                    argumentString
            );
        }
    }
}

