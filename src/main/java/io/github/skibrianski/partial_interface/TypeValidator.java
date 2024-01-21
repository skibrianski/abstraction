package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

public class TypeValidator {

    private final Map<String, Class<?>> typeParameterMap;

    public TypeValidator(Map<String, Class<?>> typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    public boolean validateArgumentTypes(
            Method implementedMethod,
            Type[] requiredParameterTypes
    ) {
        Class<?>[] parameterTypes = implementedMethod.getParameterTypes();
        if (requiredParameterTypes.length != parameterTypes.length) {
            return false;
        }
        for (int pos = 0; pos < requiredParameterTypes.length; pos++) {
            boolean argumentOk = validateType(
                    implementedMethod.getParameterTypes()[pos],
                    requiredParameterTypes[pos]
            );
            if (!argumentOk) {
                return false;
            }
        }
        return true;
    }

    public boolean validateType(Class<?> implementedType, Type type) {
        Class<?> actualType = Type.TypeParameter.class.equals(type.value())
                ? getActualTypeForTypeParameter(type)
                : type.value();
        return actualType.isAssignableFrom(implementedType);
    }

    public Class<?> getActualTypeForTypeParameter(Type type) {
        StringTruncator parameterNameTruncator = new StringTruncator(type.parameterName())
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        Class<?> baseType = typeParameterMap.get(baseParameterName);
        if (baseType == null) {
            if (baseParameterName.equals(type.parameterName())) {
                throw new PartialInterfaceNotCompletedException(
                        "cannot find type parameter: " + type.parameterName() // TODO: more detail
                );
            } else {
                // TODO: test coverage
                throw new PartialInterfaceNotCompletedException(
                        "cannot find base type parameter: " + baseParameterName // TODO: more detail
                                + " for parameter: " + type.parameterName()
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
}

