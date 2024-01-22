package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterMap;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.Map;

public class ParameterizedType extends IType {

    // handles eg: @Type("List<R>"), @Type("Map<UUID, R"), @Type("R<String>"), @Type("R<X>")

    private final Class<?> baseClass;
    private final IType[] parameters;

    public ParameterizedType(
            Class<?> baseClass,
            IType[] parameters,
            TypeParameterMap typeParameterMap
    ) {
        super(typeParameterMap);
        this.baseClass = baseClass;
        this.parameters = parameters;
    }

    @Override
    public Class<?> getActualType() {
        throw new RuntimeException("unimplemented");
    }
}
