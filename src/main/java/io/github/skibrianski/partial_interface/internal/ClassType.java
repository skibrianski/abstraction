package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterResolver;

public class ClassType<E> extends IType {

    // handles eg @Type(type = int.class), @Type(type = String.class)

    private final Class<E> klazz;

    public ClassType(Class<E> klazz, TypeParameterResolver typeParameterResolver) {
        super(typeParameterResolver);
        this.klazz = klazz;
    }

    @Override
    public Class<?> getActualType() {
        return klazz;
    }
}
