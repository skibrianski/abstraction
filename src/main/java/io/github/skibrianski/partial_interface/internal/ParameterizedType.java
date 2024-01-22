package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterResolver;

public class ParameterizedType extends IType {

    // handles eg: @Type("List<R>"), @Type("Map<UUID, R"), @Type("R<String>"), @Type("R<X>")

    private final Class<?> baseClass;
    private final IType[] parameters;

    public ParameterizedType(
            Class<?> baseClass,
            IType[] parameters,
            TypeParameterResolver typeParameterResolver
    ) {
        this.baseClass = baseClass;
        this.parameters = parameters;
    }

    @Override
    public Class<?> getActualType() {
        throw new RuntimeException("unimplemented");
    }
}
