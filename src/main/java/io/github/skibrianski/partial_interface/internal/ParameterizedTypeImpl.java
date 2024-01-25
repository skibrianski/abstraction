package io.github.skibrianski.partial_interface.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ParameterizedTypeImpl implements ParameterizedType {

    // handles eg: @Type("List<R>"), @Type("Map<UUID, R"), @Type("R<String>"), @Type("R<X>")

    private final Class<?> baseClass;
    private final Type[] parameters;

    public ParameterizedTypeImpl(
            Class<?> baseClass,
            Type[] parameters
    ) {
        this.baseClass = baseClass;
        this.parameters = parameters;
    }


    @Override
    public Class<?> getRawType() {
        return baseClass;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return parameters;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    // TODO: tests
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType otherParameterizedType = (ParameterizedType) other;
        if (!otherParameterizedType.getRawType().equals(baseClass)) {
            return false;
        }

        return Arrays.equals(getActualTypeArguments(), otherParameterizedType.getActualTypeArguments());
    }
}
