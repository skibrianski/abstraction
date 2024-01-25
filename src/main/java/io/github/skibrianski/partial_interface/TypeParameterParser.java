package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeParameterParser {

    private static final BuiltInTypeNameResolver BUILTIN_TYPE_NAME_RESOLVER = new BuiltInTypeNameResolver();

    private final TypeNameResolver typeNameResolver;

    public TypeParameterParser(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public Type parse(String typeString) {
        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            if (typeNameResolver.canResolve(typeString)) {
                return typeNameResolver.mustResolve(typeString);
            } else {
                try {
                    return classForName(typeString);
                } catch (ClassNotFoundException e) {
                    throw new PartialInterfaceException.UsageException(
                            "cannot find class: " + typeString + "."
                                    + " maybe you misspelled your type variable?"
                                    + " or try a fully qualified type like java.util.List instead?",
                            e
                    );
                }
            }
        }

        String typeVariableName = typeString.substring(0, nextOpen);
        String typeParameterArgumentsString = typeString.substring(
                nextOpen + 1,
                typeString.lastIndexOf('>')
        );
        // if input was: `Map<R, X<String>>`, typeVariableName = `Map` and typeParameterArgumentsString = `R, X<String>`
        Class<?> baseClass = resolveParameterOrBuiltIn(typeVariableName, typeNameResolver);
        Type[] typeArguments = parseList(typeParameterArgumentsString);
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return typeArguments;
            }

            @Override
            public Type getRawType() {
                return baseClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    private static Class<?> resolveParameterOrBuiltIn(String typeVariableName, TypeNameResolver typeNameResolver) {
        Class<?> baseClass = typeNameResolver.resolve(typeVariableName);
        return baseClass == null
                ? BUILTIN_TYPE_NAME_RESOLVER.mustResolve(typeVariableName)
                : baseClass;
    }

    public Type[] parseList(String typeParameterArgumentsString) {
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
        Class<?> primitiveClass = BUILTIN_TYPE_NAME_RESOLVER.resolve(name);
        return primitiveClass == null ? Class.forName(name) : primitiveClass;
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
}

