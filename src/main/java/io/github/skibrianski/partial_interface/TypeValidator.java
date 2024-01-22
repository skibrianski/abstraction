package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

public class TypeValidator {

    private final Map<String, Class<?>> typeParameterMap;

    public TypeValidator(Map<String, Class<?>> typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    public boolean hasAssignableArgumentTypes(Method implementedMethod, Type[] requiredParameterTypes) {
        Class<?>[] parameterTypes = implementedMethod.getParameterTypes();
        if (requiredParameterTypes.length != parameterTypes.length) {
            return false;
        }
        for (int pos = 0; pos < requiredParameterTypes.length; pos++) {
            if (!isAssignableType(parameterTypes[pos], requiredParameterTypes[pos])) {
                return false;
            }
        }
        return true;
    }

    public boolean isAssignableType(Class<?> implementedType, Type type) {
        Class<?> actualType = Type.NotSpecified.class.equals(type.type())
                ? getActualTypeForTypeParameter(type.value())
                : type.type();
        return actualType.isAssignableFrom(implementedType);
    }

    private Class<?> getActualTypeForTypeParameter(String typeString) {
        StringTruncator parameterNameTruncator = new StringTruncator(typeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        Class<?> baseType = typeParameterMap.get(baseParameterName);
        if (baseType == null) {
            if (baseParameterName.equals(typeString)) {
                throw new PartialInterfaceNotCompletedException(
                        "cannot find type parameter: " + typeString // TODO: more detail
                );
            } else {
                // TODO: test coverage
                throw new PartialInterfaceNotCompletedException(
                        "cannot find base type parameter: " + baseParameterName // TODO: more detail
                                + " for parameter: " + typeString
                );
            }
        }

        Class<?> actualType = baseType;
        while (arrayLevels > 0) {
            actualType = Array.newInstance(actualType, 0).getClass();
            arrayLevels--;
        }
        return actualType;
    }

    // TODO
    private static Class<?> boxClass(Class<?> primitiveClass) {
        if (primitiveClass.equals(boolean.class)) {
            return Boolean.class;
        } else if (primitiveClass.equals(byte.class)) {
            return Byte.class;
        } else if (primitiveClass.equals(char.class)) {
            return Character.class;
        } else if (primitiveClass.equals(double.class)) {
            return Double.class;
        } else if (primitiveClass.equals(float.class)) {
            return Float.class;
        } else if (primitiveClass.equals(int.class)) {
            return Integer.class;
        } else if (primitiveClass.equals(long.class)) {
            return Long.class;
        } else if (primitiveClass.equals(short.class)) {
            return Short.class;
        } else {
            throw new RuntimeException("internal error: cannot box class: " + primitiveClass.getName());
        }
    }
}

