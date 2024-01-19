package testutil;

import io.github.skibrianski.partial_interface.PartialInterface;

import java.lang.annotation.Annotation;

public final class TestUtil {

    public static PartialInterface buildPartialInterface(
            boolean isStatic,
            Class<?> returnType,
            String methodName,
            Class<?>[] argumentTypes,
            boolean lastArgumentIsVarArgs
    ) {
        return new PartialInterface() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return PartialInterface.class;
            }

            @Override
            public Class<?> returnType() {
                return returnType;
            }

            @Override
            public Class<?>[] argumentTypes() {
                return argumentTypes;
            }

            @Override
            public boolean lastArgumentIsVarArgs() {
                return lastArgumentIsVarArgs;
            }

            @Override
            public String methodName() {
                return methodName;
            }

            @Override
            public boolean isStatic() {
                return isStatic;
            }
        };
    }
}
