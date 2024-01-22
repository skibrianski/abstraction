package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;

import java.util.Map;

public abstract class IType {

    // parameterized types eg: @Type("List<R>"), @Type("Map<UUID, R"), @Type("R<String>"), @Type("R<X>")

    private final Map<String, Class<?>> typeParameterMap;

    public IType(Map<String, Class<?>> typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    protected Class<?> getTypeParameter(String name) {
        return typeParameterMap.get(name);
    }

    public abstract Class<?> getActualType();

    public static IType convertFromAnnotation(Type type, Map<String, Class<?>> typeParameterMap) {
        if (type.byClass().equals(Type.NotSpecified.class)) {
            return new TypeVariable(type.value(), typeParameterMap);
        } else {
            return new ClassType<>(type.byClass(), typeParameterMap);
        }
    }
}
