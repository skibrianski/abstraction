package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: name is wrong. as this is repeatable it should be something method-related
@Repeatable(PartialInterfaces.class)
public @interface PartialInterface {
    // TODO: support parameterized types
    Class<?> returnType();
    // TODO: support parameterized types
    Class<?>[] argumentTypes();
    // TODO: support varargs
    boolean lastArgumentIsVarArgs() default false;
    String methodName();
    boolean isStatic() default false;

    class Util {
        public static void validate(PartialInterface partialInterface) {
            if (partialInterface.argumentTypes().length == 0 && partialInterface.lastArgumentIsVarArgs()) {
                throw new PartialInterfaceUsageException(
                        // TODO:  a method where?
                        "a method with no arguments cannot be varargs"
                );
            }
        }

        public static String stringify(PartialInterface partialInterface) {
            List<String> argumentTypeList = Arrays.stream(partialInterface.argumentTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.toList());
            if (partialInterface.lastArgumentIsVarArgs()) {
                int last = argumentTypeList.size() - 1;
                String lastArgumentName = argumentTypeList.get(last);
                argumentTypeList.set(last, lastArgumentName + "...");
            }
            String argumentString = String.join(", ", argumentTypeList);
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

