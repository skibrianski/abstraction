package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import io.github.skibrianski.partial_interface.internal.ClassType;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.ParameterizedType;
import io.github.skibrianski.partial_interface.internal.TypeVariable;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeParameterParser {

    private final TypeParameterResolver typeParameterResolver;

    public TypeParameterParser(TypeParameterResolver typeParameterResolver) {
        this.typeParameterResolver = typeParameterResolver;
    }

    public IType parse(String typeString) {
        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            if (typeParameterResolver.canResolve(typeString)) {
                return new TypeVariable(typeString, typeParameterResolver);
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

        if (typeParameterResolver.canResolve(typeVariableName)) {
            baseClass = typeParameterResolver.resolve(typeVariableName);
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

    // TODO: this is a bit of am abuse of the name "TypeParameterResolver". rename TypeParameterResolver?
    private static final TypeParameterResolver primitiveResolver = new TypeParameterResolver(
            Map.ofEntries(
                    Map.entry("boolean", boolean.class),
                    Map.entry("byte", byte.class),
                    Map.entry("char", char.class),
                    Map.entry("double", double.class),
                    Map.entry("float", float.class),
                    Map.entry("int", int.class),
                    Map.entry("long", long.class),
                    Map.entry("short", short.class)
            )
    );

    private static Class<?> classForName(String name) throws ClassNotFoundException {
        Class<?> primitiveClass = primitiveResolver.resolve(name);
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

