package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeNameResolver;


public class TypeVariable extends IType {

    // handles eg @Type("R"), @Type("Z"), @Type("CONTAINER")

    private final String typeString;
    private final Class<?> klazz;

    public TypeVariable(String typeString, Class<?> klazz) {
        this.typeString = typeString;
        this.klazz = klazz;
    }

    public String name() {
        return this.typeString;
    }

    @Override
    public Class<?> getActualType() {
        return klazz;
    }
}
