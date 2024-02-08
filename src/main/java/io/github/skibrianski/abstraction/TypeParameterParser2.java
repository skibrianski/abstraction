package io.github.skibrianski.abstraction;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeParameterParser2 {

    private final TypeNameResolver typeNameResolver;

    private final TokenStream tokenStream;

    public TypeParameterParser2(String typeString, TypeNameResolver typeNameResolver) {
        final String[] withWhitespaceAroundTokens = typeString
                .replaceAll("<", " < ")
                .replaceAll(">", " > ")
                .replaceAll("\\[", " [ ")
                .replaceAll("]", " ] ")
                .replaceAll(",", " , ")
                .trim()
                .split("\\s+");
        this.tokenStream = new TokenStream(
                Arrays.stream(withWhitespaceAroundTokens)
                        .map(TypeParameterToken::of)
                        .collect(Collectors.toList())
        );
        this.typeNameResolver = typeNameResolver;
    }

    public Type parse() {
        Type type = parseInternal();
//        if (!tokenStream.isDone()) {
//            throw new RuntimeException("that's not so good"); // TODO: words, exception type
//        }
        return type;
    }

    private Type parseInternal() {
        TypeParameterToken token = tokenStream.consume();
        if (token instanceof TypeParameterToken.Variable) {
            return processVariable((TypeParameterToken.Variable) token);
        } else if (token.equals(TypeParameterToken.StaticToken.WILDCARD)) {
            return processWildcard();
        } else {
            throw new RuntimeException("assertion failed - parse() with token that was neither Variable nor Wildcard");
        }
    }

    public Type processWildcard() {
        List<Type> extensions;
        List<Type> supers;
        if (tokenStream.nextTokenIs(TypeParameterToken.StaticToken.EXTENDS)) {
            tokenStream.discard(1);
            extensions = readAndProcessList();
        } else {
            extensions = List.of();
        }
        if (tokenStream.nextTokenIs(TypeParameterToken.StaticToken.SUPER)) {
            tokenStream.discard(1);
            supers = readAndProcessList();
        } else {
            supers = List.of();
        }
        return new WildcardTypeImpl(supers.toArray(Type[]::new), extensions.toArray(Type[]::new));
    }

    public Type processVariable(TypeParameterToken.Variable token) {
        Type currentType = processBaseType(token);
        currentType = processTypeParameterList(currentType);
        currentType = processArrayDimensions(currentType);
        return currentType;
    }

    private Type processBaseType(TypeParameterToken.Variable variableToken) {
        Type currentType = typeNameResolver.resolve(variableToken.asString());
        if (currentType == null) {
            try {
                return Class.forName(variableToken.asString());
            } catch (ClassNotFoundException e) {
                throw new AbstractionException.UsageException(
                        "cannot find class: " + variableToken.asString() + "."
                                + " maybe you misspelled your type variable?"
                                + " or try a fully qualified type like java.util.List instead?",
                        e
                );
            }
        }
        return currentType;
    }

    private Type processTypeParameterList(Type currentType) {
        if (!tokenStream.nextTokenIs(TypeParameterToken.StaticToken.OPEN_PARAMETER_LIST)) {
            return currentType;
        }

        tokenStream.discard(1); // consume open
        List<Type> parameterListTypes = readAndProcessList();
        if (!tokenStream.nextTokenIs(TypeParameterToken.StaticToken.CLOSE_PARAMETER_LIST)) {
            // TODO: provide actionable detail here
            throw new AbstractionException.UsageException("illegal type parameter list");
        }
        tokenStream.discard(1); // consume close

        return new ParameterizedTypeImpl(currentType, parameterListTypes.toArray(Type[]::new));
    }

    private List<Type> readAndProcessList() {
        List<Type> parameterListTypes = new ArrayList<>();
        while (!tokenStream.isDone()) {
            parameterListTypes.add(parseInternal());
            if (tokenStream.nextTokenIs(TypeParameterToken.StaticToken.LIST_SEPARATOR)) {
                tokenStream.discard(1);
            } else {
                break;
            }
        }
        return parameterListTypes;
    }

    private Type processArrayDimensions(Type currentType) {
        while (
                tokenStream.nextTokensAre(
                        TypeParameterToken.StaticToken.OPEN_ARRAY,
                        TypeParameterToken.StaticToken.CLOSE_ARRAY
                )
        ) {
            if (currentType instanceof Class) {
                currentType = Array.newInstance((Class<?>) currentType, 0).getClass();
            } else {
                currentType = new GenericArrayTypeImpl(currentType);
            }
            tokenStream.discard(2);
        }
        return currentType;
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

