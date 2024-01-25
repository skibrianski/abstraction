package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import io.github.skibrianski.partial_interface.internal.ClassType;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.ParameterizedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeParameterParser {

    private static final BuiltInTypeNameResolver BUILTIN_TYPE_NAME_RESOLVER = new BuiltInTypeNameResolver();

    private final TypeNameResolver typeNameResolver;

    public TypeParameterParser(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public IType parse(String typeString) {
        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            if (typeNameResolver.canResolve(typeString)) {
                return new ClassType<>(typeNameResolver.mustResolve(typeString));
            } else {
                try {
                    return new ClassType<>(classForName(typeString));
                } catch (ClassNotFoundException e) {
                    throw new PartialInterfaceUsageException(
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
        // if input was: `Map<R, X<String>>`, variable will be `Map` and typeParameterArgumentsString `R, X<String>`
        Class<?> baseClass = typeNameResolver.resolve(typeVariableName);
        if (baseClass == null) {
            baseClass = BUILTIN_TYPE_NAME_RESOLVER.mustResolve(typeVariableName);
        }

        List<IType> argumentTypes = parseList(typeParameterArgumentsString);
        return new ParameterizedType(baseClass, argumentTypes);
    }

    public List<IType> parseList(String typeParameterArgumentsString) {
        String workingString = typeParameterArgumentsString;
        List<IType> argumentTypes = new ArrayList<>();
        while (true) {
            int argumentEndPos = findArgumentEndPos(workingString);
            argumentTypes.add(parse(workingString.substring(0, argumentEndPos)));
            if (argumentEndPos == workingString.length()) {
                break;
            }
            workingString = workingString.substring(argumentEndPos + 1).trim();
        }
        return argumentTypes;
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

