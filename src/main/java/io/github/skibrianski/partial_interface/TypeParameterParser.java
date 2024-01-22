package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
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
                return new ClassType<>(classForName(typeString));
            }
        }

        String typeVariableName = typeString.substring(0, nextOpen);
        String typeParameterArgumentsString = typeString.substring(
                nextOpen + 1,
                typeString.lastIndexOf('>')
        );
        // if input was: `Map<R, X<String>>`, variable will be `Map` and typeParameterArgumentsString `R, X<String>`
        Class<?> baseClass = typeParameterResolver.canResolve(typeVariableName)
                ? typeParameterResolver.resolve(typeVariableName)
                : classForName(typeVariableName);
        List<IType> argumentTypes = parseList(typeParameterArgumentsString);
        return new ParameterizedType(baseClass, argumentTypes);
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

    // TODO: probably: stop wrapping this in try-catch and let caller throw a more precise exception type?
    private static Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new PartialInterfaceException(
                    "cannot load class: " + name + "."
                            + " maybe you misspelled your type variable?"
                            + " or try a fully qualified type like java.util.List instead?"
            );
        }
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

