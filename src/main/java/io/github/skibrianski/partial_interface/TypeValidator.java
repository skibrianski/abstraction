package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;

import java.lang.reflect.Method;

public class TypeValidator {

    private final TypeNameResolver typeNameResolver;

    public TypeValidator(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public boolean hasAssignableArgumentTypes(Method implementedMethod, Type[] requiredParameterTypes) {
        Class<?>[] parameterTypes = implementedMethod.getParameterTypes(); // TODO: use getGenericParameterTypes() instead?
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
        // TODO: pass in type arguments for implementedType, compare both.
        IType internalType = IType.convertFromAnnotation(type, typeNameResolver);
        Class<?> actualType = internalType.getActualType();
        return actualType.isAssignableFrom(implementedType);
    }
}

