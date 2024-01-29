package io.github.skibrianski.abstraction;

import io.github.skibrianski.abstraction.util.StringTruncator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeParameterParser {


    private final TypeNameResolver typeNameResolver;

    public TypeParameterParser(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public Type parse(String typeString) {
        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            Type resolvedType = typeNameResolver.resolve(typeString);
            if (resolvedType != null) {
                return resolvedType;
            } else {
                try {
                    return classForName(typeString);
                } catch (ClassNotFoundException e) {
                    throw new AbstractionException.UsageException(
                            "cannot find class: " + typeString + "."
                                    + " maybe you misspelled your type variable?"
                                    + " or try a fully qualified type like java.util.List instead?",
                            e
                    );
                }
            }
        }

        StringTruncator parameterNameTruncator = new StringTruncator(typeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        int parameterClosePos = typeString.lastIndexOf('>');
        if (parameterClosePos + 1 != baseParameterName.length()) {
            throw new AbstractionException.UsageException(
                    "unsupported characters between type parameters and array indicators in: " + typeString
            );
        }

        String typeVariableName = baseParameterName.substring(0, nextOpen);
        String typeParameterArgumentsString = baseParameterName.substring(
                nextOpen + 1,
                parameterClosePos
        );

        // if input was: `Map<R, X<String>>`, typeVariableName = `Map` and typeParameterArgumentsString = `R, X<String>`
        Type baseType = typeNameResolver.resolve(typeVariableName);
        if (!(baseType instanceof Class)) {
            throw new RuntimeException("unimplemented"); // TODO: possible?
        }
        Class<?> baseClass = (Class<?>) baseType;
        Type[] typeArguments = parseList(typeParameterArgumentsString);
        Type returnType = new ParameterizedTypeImpl(baseClass, typeArguments);
        while (arrayLevels > 0) {
            returnType = new GenericArrayTypeImpl(returnType);
            arrayLevels--;
        }
        return returnType;
    }

    private Type[] parseList(String typeParameterArgumentsString) {
        String workingString = typeParameterArgumentsString;
        List<Type> argumentTypes = new ArrayList<>();
        while (true) {
            int argumentEndPos = findArgumentEndPos(workingString);
            argumentTypes.add(parse(workingString.substring(0, argumentEndPos)));
            if (argumentEndPos == workingString.length()) {
                break;
            }
            workingString = workingString.substring(argumentEndPos + 1).trim();
        }
        return argumentTypes.toArray(Type[]::new);
    }

    private static Class<?> classForName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    private static int findArgumentEndPos(String haystack) {
        int pos = 0;
        int nestCount = 0;
        while (pos < haystack.length()) {
            char c = haystack.charAt(pos);
            if (c == '<') {
                nestCount++;
            }
            if (c == '>') {
                nestCount--;
            }
            if (c == ',' && nestCount == 0) {
                return pos;
            }
            pos++;
        }
        return pos;
    }

    public static class ParameterizedTypeImpl implements ParameterizedType {
        private final Type rawType;
        private final Type[] actualTypeArguments;
        public ParameterizedTypeImpl(Type rawType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public String toString() {
            return "ParameterizedTypeImpl{rawType=" + rawType + ", actualTypeArguments=" + Arrays.toString(actualTypeArguments) + "}";
        }
    }

    public static class GenericArrayTypeImpl implements GenericArrayType {

        private final Type genericComponentType;
        public GenericArrayTypeImpl(Type genericComponentType) {
            this.genericComponentType = genericComponentType;
        }

        @Override
        public Type getGenericComponentType() {
            return genericComponentType;
        }

        @Override
        public String toString() {
            return "GenericArrayTypeImpl{genericComponentType=" + genericComponentType + "}";
        }
    }
}

