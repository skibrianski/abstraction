package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.Type;
import io.github.skibrianski.partial_interface.TypeParameterParser;
import io.github.skibrianski.partial_interface.TypeNameResolver;

// TODO: consider using java.lang.reflect types instead if they are instantiable
public abstract class IType {

    public abstract Class<?> getActualType();

    public abstract String name();

    public static java.lang.reflect.Type convertFromAnnotation(Type type, TypeNameResolver typeNameResolver) {
        if (!type.ofClass().equals(Type.NotSpecified.class)) {
            return type.ofClass();
        }
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
        return typeParameterParser.parse(type.value());
    }
}
