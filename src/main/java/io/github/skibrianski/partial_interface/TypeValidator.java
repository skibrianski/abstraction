package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class TypeValidator {

    private final TypeNameResolver typeNameResolver;

    public TypeValidator(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public boolean hasAssignableArgumentTypes(Method implementedMethod, Type[] requiredParameterTypes) {
        java.lang.reflect.Type[] parameterTypes = implementedMethod.getGenericParameterTypes();
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


    public boolean isAssignableType(java.lang.reflect.Type implementedType, Type type) {
        IType internalType = IType.convertFromAnnotation(type, typeNameResolver);
        Class<?> actualType = internalType.getActualType();

        // note: options are Class, GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
        // because we expect concrete types here, we know it cannot be TypeVariable here.
        // it can however be a WildcardType eg `? extends Number` or ParameterizedType eg `List<Integer>`
        // not sure about GenericArrayType
        if (implementedType instanceof Class) {
            return actualType.isAssignableFrom((Class<?>) implementedType);
        }
        ParameterizedType g;

        // TODO:

        // note: this is yucky because
        // Map<Number, String> cannot be fulfilled by HashMap<Integer, String>
        //  but Map<? extends Number, String> CAN
        throw new RuntimeException("unimplemented");
    }
}

