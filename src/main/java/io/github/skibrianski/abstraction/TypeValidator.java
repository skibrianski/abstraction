package io.github.skibrianski.abstraction;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return hasAssignableArgumentTypes(requiredParameterTypesConv, implementedParameterTypes);
    }

    public boolean isAssignableType(Type requiredType, java.lang.reflect.Type implementedType) {
        return isAssignableType(convertFromAnnotation(requiredType), implementedType);
    }

    public java.lang.reflect.Type convertFromAnnotation(Type type) {
        if (!type.ofClass().equals(Type.NotSpecified.class)) {
            return type.ofClass();
        }
        return new TypeParameterParser(type.value(), typeNameResolver).parse();
    }

    public static boolean isAssignableType(java.lang.reflect.Type requiredType, java.lang.reflect.Type implementedType) {
        // note: a Type can be a Class, GenericArrayType, ParameterizedType, TypeVariable<D>, or WildcardType
        // we expect concrete types here so no need to worry about TypeVariable
        if (requiredType instanceof ParameterizedType) {
            return isAssignableToParameterizedType((ParameterizedType) requiredType, implementedType);
        } else if (requiredType instanceof GenericArrayType) {
            return isAssignableToArray((GenericArrayType) requiredType, implementedType);
        } else if (requiredType instanceof WildcardType) {
            return isAssignableToWildcard((WildcardType) requiredType, implementedType);
        } else if (requiredType instanceof Class) {
            return isAssignableToClass((Class<?>) requiredType, implementedType);
        }
        throw new RuntimeException("unimplemented");
    }

    private static boolean isAssignableToClass(
            Class<?> requiredClass,
            java.lang.reflect.Type implementedType
    ) {
        if (implementedType instanceof Class<?>) {
            return requiredClass.isAssignableFrom((Class<?>) implementedType);
        } else if (implementedType instanceof WildcardType) {
            return isAssignableToClassFromWildcardType(requiredClass, (WildcardType) implementedType);
        }

        throw new RuntimeException("unimplemented");
    }

    private static boolean isAssignableToClassFromWildcardType(
            Class<?> requiredClass,
            WildcardType implementedWildcardType
    ) {
        for (java.lang.reflect.Type upperBound : implementedWildcardType.getLowerBounds()) {
            if (isAssignableType(requiredClass, upperBound)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAssignableToWildcard(
            WildcardType requiredWildcardType,
            java.lang.reflect.Type implementedType
    ) {
        if (implementedType instanceof Class<?>) {
            return isAssignableToWildcardTypeFromClass(
                    requiredWildcardType, (Class<?>) implementedType
            );
        }
        // TODO: support WildcardType <- WildcardType checks
        throw new RuntimeException("unimplemented");
    }

    private static boolean isAssignableToParameterizedType(
            ParameterizedType requiredParameterizedType,
            java.lang.reflect.Type implementedType
    ) {
        if (implementedType instanceof Class<?>) {
            return isAssignableToParameterizedTypeFromClass(
                    requiredParameterizedType,
                    (Class<?>) implementedType
            );
        } else if (implementedType instanceof ParameterizedType) {
            return isAssignableToParameterizedTypeFromParameterizedType(
                    requiredParameterizedType,
                    (ParameterizedType) implementedType
            );
        } else if (implementedType instanceof GenericArrayType) {
            return false;
        }
        throw new RuntimeException("unimplemented");
    }

    private static boolean isAssignableToWildcardTypeFromClass(
            WildcardType requiredWildcardType,
            Class<?> implementedType
    ) {
        for (java.lang.reflect.Type upperBound : requiredWildcardType.getUpperBounds()) {
            if (!isAssignableType(upperBound, implementedType)) {
                return false;
            }
        }
        for (java.lang.reflect.Type lowerBound : requiredWildcardType.getLowerBounds()) {
            if (!isAssignableType(implementedType, lowerBound)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignableToParameterizedTypeFromClass(
            ParameterizedType requiredType,
            Class<?> implementedType
    ) {
        // handles the case of e.g. lowerBound = Enum<T> or lowerBound = Comparable<T>
        AnnotatedType superClassAnnotatedType = implementedType.getAnnotatedSuperclass();
        Set<AnnotatedType> superAnnotatedTypeSet = Stream.concat(
                Arrays.stream(implementedType.getAnnotatedInterfaces()),
                superClassAnnotatedType == null ? Stream.of() : Stream.of(superClassAnnotatedType)
        ).collect(Collectors.toSet());
        for (AnnotatedType superAnnotatedType : superAnnotatedTypeSet) {
            java.lang.reflect.Type superClassType = superAnnotatedType == null
                    ? null
                    : superAnnotatedType.getType();

            if (superClassType instanceof ParameterizedType) {
                ParameterizedType superClassParameterizedType = (ParameterizedType) superClassType;
                return superClassParameterizedType.getRawType().equals(requiredType.getRawType())
                        && Arrays.equals(
                                superClassParameterizedType.getActualTypeArguments(),
                                requiredType.getActualTypeArguments()
                        );
            }
        }

        return false;
    }

    private static boolean isAssignableToParameterizedTypeFromParameterizedType(
            ParameterizedType requiredParameterizedType,
            ParameterizedType implementedParameterizedType
    ) {
        if (!parameterizedTypeHasAssignableRawType(requiredParameterizedType, implementedParameterizedType)) {
            return false;
        }

        return hasAssignableArgumentTypes(
                Arrays.stream(requiredParameterizedType.getActualTypeArguments())
                        .map(TypeValidator::possiblyBox)
                        .toArray(java.lang.reflect.Type[]::new), implementedParameterizedType.getActualTypeArguments()
        );
    }

    private static boolean hasAssignableArgumentTypes(
            java.lang.reflect.Type[] requiredTypeParameters,
            java.lang.reflect.Type[] implementedTypeParameters
    ) {
        if (implementedTypeParameters.length != requiredTypeParameters.length) {
            return false;
        }

        for (int pos = 0; pos < implementedTypeParameters.length; pos++) {
            if (!isAssignableType(requiredTypeParameters[pos], implementedTypeParameters[pos])) {
                return false;
            }
        }
        return true;
    }

    private static boolean parameterizedTypeHasAssignableRawType(
            ParameterizedType requiredType,
            ParameterizedType implementedType
    ) {
        Class<?> baseRequiredClass = (Class<?>) requiredType.getRawType();
        Class<?> baseImplementedClass = (Class<?>) implementedType.getRawType();
        return baseRequiredClass.isAssignableFrom(baseImplementedClass);
    }

    private static boolean isAssignableToArray(GenericArrayType requiredType, java.lang.reflect.Type implementedType) {
        if (!(implementedType instanceof GenericArrayType)) {
            throw new RuntimeException("well that won't work"); // TODO: words
        }
        return isAssignableType(
                requiredType.getGenericComponentType(), ((GenericArrayType) implementedType).getGenericComponentType()
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

