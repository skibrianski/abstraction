package testutil;

import io.github.skibrianski.partial_interface.RequiresChildMethod;

import java.lang.annotation.Annotation;

public final class TestUtil {

    public static RequiresChildMethod.Type buildRegularType(Class<?> classType) {
        return new RequiresChildMethod.Type() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresChildMethod.Type.class;
            }


            @Override
            public Class<?> value() {
                return classType;
            }

            @Override
            public RequiresChildMethod.TypeType type() {
                return RequiresChildMethod.TypeType.REGULAR;
            }
        };
    }

    public static RequiresChildMethod buildAnnotation(
            boolean isStatic,
            RequiresChildMethod.Type returnType,
            String methodName,
            Class<?>[] argumentTypes
    ) {
        return new RequiresChildMethod() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresChildMethod.class;
            }

            @Override
            public Type returnType() {
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
