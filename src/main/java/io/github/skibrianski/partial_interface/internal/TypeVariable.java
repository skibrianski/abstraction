package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterResolver;


public class TypeVariable extends IType {

    // handles eg @Type("R"), @Type("Z"), @Type("CONTAINER")

    private final String typeString;
    private final Class<?> klazz;

    public TypeVariable(String typeString, TypeParameterResolver typeParameterResolver) {
        this.typeString = typeString;
        this.klazz = typeParameterResolver.mustResolve(typeString);
    }

    public String name() {
        return this.typeString;
    }

    @Override
    public Class<?> getActualType() {
        return klazz;
    }
}
