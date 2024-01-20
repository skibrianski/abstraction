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
        };
    }

    public static RequiresChildMethod buildAnnotation(
            boolean isStatic,
            RequiresChildMethod.Type returnType,
            String methodName,
            RequiresChildMethod.Type[] argumentTypes
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
            public Type[] argumentTypes() {
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
