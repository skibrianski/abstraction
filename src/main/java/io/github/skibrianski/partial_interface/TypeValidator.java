package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.ParameterizedType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return isAssignableType(implementedType, IType.convertFromAnnotation(type, typeNameResolver));

    }

    public boolean isAssignableType(java.lang.reflect.Type implementedType, java.lang.reflect.Type requiredType) {
        // note: options are Class, GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
        // because we expect concrete types here, we know it cannot be TypeVariable here.
        // it can however be a WildcardType eg `? extends Number` or ParameterizedType eg `List<Integer>`
        // not sure about GenericArrayType
        if (implementedType instanceof Class) {
            return ((Class<?>) requiredType).isAssignableFrom((Class<?>) implementedType);
        } else if (implementedType instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType implementedParameterizedType =
                    (java.lang.reflect.ParameterizedType) implementedType;
            if (!(requiredType instanceof java.lang.reflect.ParameterizedType)) {
                throw new RuntimeException("unimplemented"); // TODO: possible?
            }
            java.lang.reflect.ParameterizedType requiredParameterizedType =
                    (java.lang.reflect.ParameterizedType) requiredType;
            Class<?> baseImplementedClass = (Class<?>) implementedParameterizedType.getRawType();
            Class<?> baseRequiredClass = (Class<?>) requiredParameterizedType.getRawType();

            if (!baseRequiredClass.isAssignableFrom(baseImplementedClass)) {
                return false;
            }

            java.lang.reflect.Type[] implementedTypeParameters = implementedParameterizedType.getActualTypeArguments();
            java.lang.reflect.Type[] requiredTypeParameters = requiredParameterizedType.getActualTypeArguments();
            if (implementedTypeParameters.length != requiredTypeParameters.length) {
                return false;
            }

            for (int pos = 0; pos < implementedTypeParameters.length; pos++) {
                if (!isAssignableType(implementedTypeParameters[pos], requiredTypeParameters[pos])) {
                    return false;
                }
            }
            return true;

        } // TODO: wildcard types with bounds, eg Map<Number, String> cannot be fulfilled by HashMap<Integer, String>
        //  but Map<? extends Number, String> CAN
        throw new RuntimeException("unimplemented: parameterized type");
    }
}

