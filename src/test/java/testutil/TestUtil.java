package testutil;

import io.github.skibrianski.partial_interface.RequiresChildMethod;

import java.lang.annotation.Annotation;

public final class TestUtil {

    public static RequiresChildMethod buildAnnotation(
            boolean isStatic,
            Class<?> returnType,
            String methodName,
            Class<?>[] argumentTypes
    ) {
        return new RequiresChildMethod() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresChildMethod.class;
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
