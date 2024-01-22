package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

public class TypeValidator {

    private final TypeParameterMap typeParameterMap;

    public TypeValidator(TypeParameterMap typeParameterMap) {
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
        IType internalType = IType.convertFromAnnotation(type, typeParameterMap);
        Class<?> actualType = internalType.getActualType();
        return actualType.isAssignableFrom(implementedType);
    }
}

