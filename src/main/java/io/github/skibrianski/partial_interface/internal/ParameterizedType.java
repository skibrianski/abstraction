package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterResolver;

import java.util.List;

public class ParameterizedType extends IType {

    // handles eg: @Type("List<R>"), @Type("Map<UUID, R"), @Type("R<String>"), @Type("R<X>")

    private final Class<?> baseClass;
    private final List<IType> parameters;

    public ParameterizedType(
            Class<?> baseClass,
            List<IType> parameters
    ) {
        this.baseClass = baseClass;
        this.parameters = parameters;
    }

    @Override
    public String name() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Class<?> getActualType() {
        throw new RuntimeException("unimplemented");
    }
}
