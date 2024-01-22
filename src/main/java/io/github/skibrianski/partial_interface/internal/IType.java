package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;
import io.github.skibrianski.partial_interface.TypeParameterParser;
import io.github.skibrianski.partial_interface.TypeParameterResolver;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IType {


    // TODO: this probably doesn't belong here, but rather in our parser

    public IType() { }

    public abstract Class<?> getActualType();

    public abstract String name();

    public static IType convertFromAnnotation(Type type, TypeParameterResolver typeParameterResolver) {
        if (!type.byClass().equals(Type.NotSpecified.class)) {
            return new ClassType<>(type.byClass());
        }
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeParameterResolver);
        return typeParameterParser.parse(type.value());
    }
}
