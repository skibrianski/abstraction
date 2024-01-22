package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import io.github.skibrianski.partial_interface.internal.ClassType;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.TypeVariable;

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
                return new TypeVariable(typeString, typeNameResolver);
            } else {
                try {
                    return new ClassType<>(classForName(typeString));
                } catch (ClassNotFoundException e) {
                    throw new PartialInterfaceUsageException(
                            "cannot find class: " + typeString + "."
                                    + " maybe you misspelled your type variable?"
                                    + " or try a fully qualified type like java.util.List instead?"
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
        Class<?> baseClass;

        if (typeNameResolver.canResolve(typeVariableName)) {
            baseClass = typeNameResolver.resolve(typeVariableName);
        } else {
            // wrong. we want type params, too
//            try {
//                baseClass = classForName(typeVariableName);
//            } catch (ClassNotFoundException e) {
//                throw new PartialInterfaceUsageException(
//                        "cannot find class: " + typeString + "."
//                                + " maybe you misspelled your type variable?"
//                                + " or try a fully qualified type like java.util.List instead?"
//                );
//            }
        }
//        List<IType> argumentTypes = parseList(typeParameterArgumentsString);
//        return new ParameterizedType(baseClass, argumentTypes);
        return null;
    }

    public List<IType> parseList(String typeParameterArgumentString) {
        String workingString = typeParameterArgumentString;
        int argumentEndPos = findArgumentEndPos(workingString);
        List<IType> argumentTypes = new ArrayList<>();
        while (argumentEndPos != -1) {
            String typeParameterString = typeParameterArgumentString.substring(0, argumentEndPos);
            IType type = parse(typeParameterString);
            argumentTypes.add(type);
            workingString = workingString.substring(argumentEndPos + 1).trim();
            argumentEndPos = findArgumentEndPos(workingString);
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
        while (true) {
            char c = haystack.charAt(pos);
            // TOOD: use switch instead, probably.
            if (c == '<') {
                nestCount++;
            }
            if (c == '>') {
                if (nestCount == 0) {
                    return pos;
                }
                nestCount--;
            }
            if (c == ',' && nestCount == 0) {
                return pos;
            }
            pos++;
        }
    }
}

