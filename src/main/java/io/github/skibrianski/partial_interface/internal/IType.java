package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;
import io.github.skibrianski.partial_interface.TypeParameterMap;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;

import java.util.Map;

public abstract class IType {


    // TODO: this probably doesn't belong here, but rather in our parser
    private final TypeParameterMap typeParameterMap;

    public IType(TypeParameterMap typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    protected Class<?> getTypeParameter(String name) {
        return typeParameterMap.resolve(name);
    }

    public abstract Class<?> getActualType();

    public static IType convertFromAnnotation(Type type, TypeParameterMap typeParameterMap) {
        if (!type.byClass().equals(Type.NotSpecified.class)) {
            return new ClassType<>(type.byClass(), typeParameterMap);
        }
        return parse(type.value(), typeParameterMap);
    }

    public static IType parse(
            String typeString,
            TypeParameterMap typeParameterMap
    ) {
        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            // TODO: typeParameterMap needs to be a bit smarter and handle de-arrayification
            if (typeParameterMap.canResolve(typeString)) {
                // TODO: resolve the variable?
                return new TypeVariable(typeString, typeParameterMap);
            } else {
                return new ClassType<>(classForName(typeString), typeParameterMap);
            }
        }

        String variable = typeString.substring(0, nextOpen);
        String typeParameterArgumentsString = typeString.substring(
                nextOpen + 1,
                typeString.lastIndexOf('>')
        );
        // if input was: `Map<R, X<String>>`, variable will be `Map` and typeParameterArgumentsString `R, X<String>`
        Class<?> baseClass = classForName(variable);
            // TODO: we now have `Foo, Map<UUID, Bar>, Baz` or something. call parseList(typeParameterArgumentsString)
        throw new RuntimeException("not implemented");
    }

    private static Class<?> classForName(String name) {
        try {
            return Class.forName(name);
//            return new ParameterizedType(baseClass, ...);
        } catch (ClassNotFoundException e) {
            throw new PartialInterfaceException(
                    "cannot load class: " + name + "."
                            + " maybe you misspelled your type variable?"
                            + " or try a fully qualified type like java.util.List instead?"
            );
        }
    }

    public static IType[] parseList(String typeParameterArgumentString, Map<String, Class<?>> typeParameterMap) {
        int nextComma = notFoundIsMaxInt(typeParameterArgumentString.indexOf(','));
        int nextClose = notFoundIsMaxInt(typeParameterArgumentString.indexOf('>'));
        if (nextComma < nextClose) {
            String typeParameterString = typeParameterArgumentString.substring(0, nextComma);
            // there's another arg
        } else {
            String typeParameterString = typeParameterArgumentString.substring(0, nextClose);
            // this is the last arg
        }
        throw new RuntimeException("not implemented");
    }

    private static int notFoundIsMaxInt(int input) {
        return input == -1 ? Integer.MAX_VALUE : input;
    }
}
