package testutil;

import io.github.skibrianski.abstraction.RequiresChildMethod;
import io.github.skibrianski.abstraction.Type;

import java.lang.annotation.Annotation;

public final class TestUtil {

    public static Type buildParameterizedType(String parameterName) {
        return buildType(Type.NotSpecified.class, parameterName);
    }

    public static Type buildRegularType(Class<?> classType) {
        return buildType(classType, "");
    }

    private static Type buildType(Class<?> classType, String parameterName) {
        return new Type() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Type.class;
            }


            @Override
            public Class<?> ofClass() {
                return classType;
            }

            @Override
            public String value() {
                return parameterName;
            }
        };
    }

    public static RequiresChildMethod buildRequiresChildMethod(
            boolean isStatic,
            Type returnType,
            String methodName,
            Type[] argumentTypes
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
