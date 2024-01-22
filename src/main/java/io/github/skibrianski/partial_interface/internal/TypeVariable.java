package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.TypeParameterMap;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.Map;

public class TypeVariable extends IType {

    // handles eg @Type("R"), @Type("Z"), @Type("CONTAINER")

    private final String typeString;
    private final Class<?> klazz;

    public TypeVariable(String typeString, TypeParameterMap typeParameterMap) {
        super(typeParameterMap);
        this.typeString = typeString;
        this.klazz = typeParameterMap.mustResolve(typeString);
    }

    public String name() {
        return this.typeString;
    }

    @Override
    public Class<?> getActualType() {
        return klazz;
    }
}
