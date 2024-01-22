package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterMap;

import java.util.Map;

public class ClassType<E> extends IType {

    // handles eg @Type(type = int.class), @Type(type = String.class)

    private final Class<E> klazz;

    public ClassType(Class<E> klazz, TypeParameterMap typeParameterMap) {
        super(typeParameterMap);
        this.klazz = klazz;
    }

    @Override
    public Class<?> getActualType() {
        return klazz;
    }
}
