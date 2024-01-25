package io.github.skibrianski.partial_interface;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

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
        return isAssignableType(implementedType, convertFromAnnotation(type));

    }

    public java.lang.reflect.Type convertFromAnnotation(Type type) {
        if (!type.ofClass().equals(Type.NotSpecified.class)) {
            return type.ofClass();
        }
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
        return typeParameterParser.parse(type.value());
    }

    public boolean isAssignableType(java.lang.reflect.Type implementedType, java.lang.reflect.Type requiredType) {
        // note: options are Class, GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
        // because we expect concrete types here, we know it cannot be TypeVariable here.
        // it can however be a WildcardType eg `? extends Number` or ParameterizedType eg `List<Integer>`
        // not sure about GenericArrayType
        if (implementedType instanceof Class) {
            return ((Class<?>) requiredType).isAssignableFrom((Class<?>) implementedType);
        } else if (implementedType instanceof ParameterizedType) {
            ParameterizedType implementedParameterizedType = (ParameterizedType) implementedType;
            if (!(requiredType instanceof ParameterizedType)) {
                throw new RuntimeException("unimplemented"); // TODO: possible?
            }
            ParameterizedType requiredParameterizedType = (ParameterizedType) requiredType;
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
                java.lang.reflect.Type requiredTypeParameter = possiblyBox(requiredTypeParameters[pos]);
                if (!isAssignableType(implementedTypeParameters[pos], requiredTypeParameter)) {
                    return false;
                }
            }
            return true;

        } // TODO: wildcard types with bounds, eg Map<Number, String> cannot be fulfilled by HashMap<Integer, String>
        //  but Map<? extends Number, String> CAN
        throw new RuntimeException("unimplemented: parameterized type");
    }

    private static java.lang.reflect.Type possiblyBox(java.lang.reflect.Type unboxed) {
        if (unboxed.equals(boolean.class)) {
            return Boolean.class;
        } else if (unboxed.equals(byte.class)) {
            return Byte.class;
        } else if (unboxed.equals(char.class)) {
            return Character.class;
        } else if (unboxed.equals(double.class)) {
            return Double.class;
        } else if (unboxed.equals(float.class)) {
            return Float.class;
        } else if (unboxed.equals(int.class)) {
            return Integer.class;
        } else if (unboxed.equals(long.class)) {
            return Long.class;
        } else if (unboxed.equals(short.class)) {
            return Short.class;
        } else {
            return unboxed;
        }
    }
}

