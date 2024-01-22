package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;

import java.lang.reflect.Method;

public class TypeValidator {

    private final TypeParameterResolver typeParameterResolver;

    public TypeValidator(TypeParameterResolver typeParameterResolver) {
        this.typeParameterResolver = typeParameterResolver;
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
        IType internalType = IType.convertFromAnnotation(type, typeParameterResolver);
        Class<?> actualType = internalType.getActualType();
        return actualType.isAssignableFrom(implementedType);
    }
}

