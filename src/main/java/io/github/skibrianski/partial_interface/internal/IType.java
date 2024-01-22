package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;

import java.util.Map;

public abstract class IType {


    // TODO: this probably doesn't belong here, but rather in our parser
    private final Map<String, Class<?>> typeParameterMap;

    public IType(Map<String, Class<?>> typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    protected Class<?> getTypeParameter(String name) {
        return typeParameterMap.get(name);
    }

    public abstract Class<?> getActualType();

    public static IType convertFromAnnotation(Type type, Map<String, Class<?>> typeParameterMap) {
        return parse(type.byClass(), type.value(), typeParameterMap);
    }

    public static IType parse(
            Class<?> typeClass,
            String typeString,
            Map<String, Class<?>> typeParameterMap
    ) {
        if (!typeClass.equals(Type.NotSpecified.class)) {
            return new ClassType<>(typeClass, typeParameterMap);
        }

        int nextOpen = typeString.indexOf('<');
        if (nextOpen == -1) {
            return new TypeVariable(typeString, typeParameterMap);
        }

        String variable = typeString.substring(0, nextOpen);
        String typeParameterArgumentsString = typeString.substring(
                nextOpen + 1,
                typeString.lastIndexOf('>')
        );
        // if input was: `Map<R, X<String>>`, variable will be `Map` and typeParameterArgumentsString `R, X<String>`
        try {
            Class<?> baseClass = Class.forName(variable);
            // TODO: we now have `Foo, Map<UUID, Bar>, Baz` or something. call parseList(typeParameterArgumentsString)
//            return new ParameterizedType(baseClass, ...);
        } catch (ClassNotFoundException e) {
            throw new PartialInterfaceUsageException(
                    "cannot load class: " + variable + ", try a fully qualified type like java.util.List instead"
            );
        }
        throw new RuntimeException("not implemented");
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
