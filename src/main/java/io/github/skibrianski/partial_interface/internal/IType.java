package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;

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
        if (!type.byClass().equals(Type.NotSpecified.class)) {
            return new ClassType<>(type.byClass(), typeParameterMap);
        }

        String typeString = type.value();
        if (!typeString.contains("<")) {
            return new TypeVariable(typeString, typeParameterMap);
        }

        if (typeString.matches("^[^<]+<[^>]+>$")) {
            throw new RuntimeException("unimplemented - match");
        }
        throw new RuntimeException("unimplemented");
    }
}
