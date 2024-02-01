package io.github.skibrianski.abstraction;

import io.github.skibrianski.abstraction.util.StringTruncator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeParameterParser {


    private final TypeNameResolver typeNameResolver;

    public TypeParameterParser(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public Type parse(String typeString) {
        String trimmedTypeString = typeString.trim();
        // TODO: support multiple extends / supers
        boolean isExtends = false;
        boolean isSuper = false;
        if (trimmedTypeString.startsWith("?")) {
            int pos = 1;
            while (trimmedTypeString.charAt(pos) == ' ') {
                pos++;
            }
            isExtends = trimmedTypeString.substring(pos).startsWith("extends");
            if (isExtends) {
                trimmedTypeString = trimmedTypeString.substring(pos + "extends".length()).trim();
            } else {
                isSuper = trimmedTypeString.substring(pos).startsWith("super");
                if (isSuper) {
                    trimmedTypeString = trimmedTypeString.substring(pos + "super".length()).trim();
                }
            }

            if (!isExtends && !isSuper) {
                // TODO: write a TypeParameterParserTest test for this
                throw new AbstractionException.UsageException("unsupported wildcard syntax: " + typeString);
            }
        }

        int nextOpen = trimmedTypeString.indexOf('<');
        if (nextOpen == -1) {
            Type resolvedType = typeNameResolver.resolve(trimmedTypeString);
            if (resolvedType != null) {
                if (isExtends) {
                    // TODO: write a TypeParameterParserTest test for this
                    return new WildcardTypeImpl(new Type[]{}, new Type[]{resolvedType});
                }
                if (isSuper) {
                    // TODO: write a TypeParameterParserTest test for this
                    return new WildcardTypeImpl(new Type[]{resolvedType}, new Type[]{});
                }
                return resolvedType;
            } else {
                try {
                    return classForName(trimmedTypeString);
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

        StringTruncator parameterNameTruncator = new StringTruncator(trimmedTypeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        int parameterClosePos = trimmedTypeString.lastIndexOf('>');
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
        if (baseType == null) {
            throw new AbstractionException.UsageException(
                    "cannot load base type for variable: " + typeVariableName
                            + ". try including the package path?"
            );
        }
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
        if (isExtends) {
            // TODO: write a TypeParameterParserTest test for this
            return new WildcardTypeImpl(new Type[]{}, new Type[]{returnType});
        }
        if (isSuper) {
            // TODO: write a TypeParameterParserTest test for this
            return new WildcardTypeImpl(new Type[]{returnType}, new Type[]{});
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

    public static class WildcardTypeImpl implements WildcardType {

        private final Type[] lowerBounds;
        private final Type[] upperBounds;

        public WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }

        @Override
        public String toString() {
            return "WildcardTypeImpl{lowerBounds=" +
                    Arrays.toString(lowerBounds) +
                    ", upperBounds=" +
                    Arrays.toString(upperBounds) +
                    "}";
        }
    }
}

