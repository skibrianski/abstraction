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

    public boolean isAssignableType(java.lang.reflect.Type implementedType, IType requiredType) {
        // note: options are Class, GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
        // because we expect concrete types here, we know it cannot be TypeVariable here.
        // it can however be a WildcardType eg `? extends Number` or ParameterizedType eg `List<Integer>`
        // not sure about GenericArrayType
        if (implementedType instanceof Class) {
            return requiredType.getActualType().isAssignableFrom((Class<?>) implementedType);
        } else if (implementedType instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType implementedParameterizedType =
                    (java.lang.reflect.ParameterizedType) implementedType;
            if (!(requiredType instanceof ParameterizedType)) {
                throw new RuntimeException("unimplemented"); // TODO: possible?
            }
            ParameterizedType requiredParameterizedType = (ParameterizedType) requiredType;
            Class<?> baseImplementedClass = (Class<?>) implementedParameterizedType.getRawType();
            Class<?> baseRequiredClass = requiredType.getActualType();

            if (!baseRequiredClass.isAssignableFrom(baseImplementedClass)) {
                return false;
            }

            List<java.lang.reflect.Type> implementedTypeParameters =
                    Arrays.stream(implementedParameterizedType.getActualTypeArguments()).collect(Collectors.toList());
            List<IType> requiredTypeParameters = requiredParameterizedType.getParameters();
            if (implementedTypeParameters.size() != requiredTypeParameters.size()) {
                return false;
            }

            for (int pos = 0; pos < implementedTypeParameters.size(); pos++) {
                if (!isAssignableType(implementedTypeParameters.get(pos), requiredTypeParameters.get(pos))) {
                    return false;
                }
            }
            return true;

        } // TODO: wildcard types with bounds, eg Map<Number, String> cannot be fulfilled by HashMap<Integer, String>
        //  but Map<? extends Number, String> CAN
        throw new RuntimeException("unimplemented: parameterized type");
    }
}

