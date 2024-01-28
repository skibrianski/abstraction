package io.github.skibrianski.partial_interface;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class TypeValidator {

    private final TypeNameResolver typeNameResolver;

    public TypeValidator(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public boolean hasAssignableArgumentTypes(Method implementedMethod, Type[] requiredParameterTypes) {
        java.lang.reflect.Type[] implementedParameterTypes = implementedMethod.getGenericParameterTypes();
        java.lang.reflect.Type[] requiredParameterTypesConv = Arrays.stream(requiredParameterTypes)
                .map(this::convertFromAnnotation)
                .toArray(java.lang.reflect.Type[]::new);
        return hasAssignableArgumentTypes(implementedParameterTypes, requiredParameterTypesConv);
    }

    public boolean isAssignableType(java.lang.reflect.Type implementedType, Type type) {
        return isAssignableType(implementedType, convertFromAnnotation(type));
    }

    public java.lang.reflect.Type convertFromAnnotation(Type type) {
        if (!type.ofClass().equals(Type.NotSpecified.class)) {
            return type.ofClass();
        }
        return typeNameResolver.getTypeParameterParser().parse(type.value());
    }

    public boolean isAssignableType(java.lang.reflect.Type implementedType, java.lang.reflect.Type requiredType) {
        // note: a Type can be a Class, GenericArrayType, ParameterizedType, TypeVariable<D>, or WildcardType
        // because we expect concrete types here, we know it cannot be TypeVariable here.
        if (implementedType instanceof Class) {
            return ((Class<?>) requiredType).isAssignableFrom((Class<?>) implementedType);
        } else if (implementedType instanceof ParameterizedType) {
            return isAssignableParameterizedType((ParameterizedType) implementedType, requiredType);
        } else if (implementedType instanceof GenericArrayType) {
            return isAssignableArray((GenericArrayType) implementedType, requiredType);
        }

        // TODO: wildcard types with bounds, eg Map<Number, String> cannot be fulfilled by HashMap<Integer, String>
        //  but Map<? extends Number, String> CAN
        throw new RuntimeException("unimplemented");
    }

    public boolean isAssignableParameterizedType(
            ParameterizedType implementedType,
            java.lang.reflect.Type requiredType
    ) {
        if (!(requiredType instanceof ParameterizedType)) {
            throw new RuntimeException("unimplemented"); // TODO: possible?
        }
        ParameterizedType requiredParameterizedType = (ParameterizedType) requiredType;

        if (!parameterizedTypeHasAssignableRawType(implementedType, requiredParameterizedType)) {
            return false;
        }

        return hasAssignableArgumentTypes(
                implementedType.getActualTypeArguments(),
                Arrays.stream(requiredParameterizedType.getActualTypeArguments())
                        .map(TypeValidator::possiblyBox)
                        .toArray(java.lang.reflect.Type[]::new)
        );
    }

    public boolean hasAssignableArgumentTypes(
            java.lang.reflect.Type[] implementedTypeParameters,
            java.lang.reflect.Type[] requiredTypeParameters
    ) {
        if (implementedTypeParameters.length != requiredTypeParameters.length) {
            return false;
        }

        for (int pos = 0; pos < implementedTypeParameters.length; pos++) {
            if (!isAssignableType(implementedTypeParameters[pos], requiredTypeParameters[pos])) {
                return false;
            }
        }
        return true;
    }

    public boolean parameterizedTypeHasAssignableRawType(
            ParameterizedType implementedType,
            ParameterizedType requiredType
    ) {
        Class<?> baseImplementedClass = (Class<?>) implementedType.getRawType();
        Class<?> baseRequiredClass = (Class<?>) requiredType.getRawType();
        return baseRequiredClass.isAssignableFrom(baseImplementedClass);
    }

    public boolean isAssignableArray(GenericArrayType implementedType, java.lang.reflect.Type requiredType) {
        if (!(requiredType instanceof GenericArrayType)) {
            throw new RuntimeException("well that won't work"); // TODO: words
        }
        GenericArrayType requiredArrayType = (GenericArrayType) requiredType;
        return isAssignableType(
                implementedType.getGenericComponentType(),
                requiredArrayType.getGenericComponentType()
        );
    }

    private static java.lang.reflect.Type possiblyBox(java.lang.reflect.Type possiblyPrimitive) {
        if (possiblyPrimitive.equals(boolean.class)) {
            return Boolean.class;
        } else if (possiblyPrimitive.equals(byte.class)) {
            return Byte.class;
        } else if (possiblyPrimitive.equals(char.class)) {
            return Character.class;
        } else if (possiblyPrimitive.equals(double.class)) {
            return Double.class;
        } else if (possiblyPrimitive.equals(float.class)) {
            return Float.class;
        } else if (possiblyPrimitive.equals(int.class)) {
            return Integer.class;
        } else if (possiblyPrimitive.equals(long.class)) {
            return Long.class;
        } else if (possiblyPrimitive.equals(short.class)) {
            return Short.class;
        }
        return possiblyPrimitive;
    }
}

