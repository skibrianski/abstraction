package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeNameResolver {

    private final Map<String, java.lang.reflect.Type> typeMap;
    private final Set<String> typeParameterNames;
    private final TypeParameterParser typeParameterParser;

    public TypeNameResolver() {
        this.typeMap = BuiltInTypes.builtInClassMap();
        this.typeParameterNames = new HashSet<>();
        this.typeParameterParser = new TypeParameterParser(this);
    }

    public TypeNameResolver addTypeParameter(String name, java.lang.reflect.Type type) {
        typeMap.put(name, type);
        typeParameterNames.add(name);
        return this;
    }

    public TypeParameterParser getTypeParameterParser() {
        return this.typeParameterParser;
    }

    public boolean canResolve(String typeString) {
        return resolve(typeString, false) != null;
    }

    // returns null on failure to lookup
    public java.lang.reflect.Type resolve(String typeString) {
        return resolve(typeString, false);
    }

    // throws Exception on failure to lookup
    public java.lang.reflect.Type mustResolve(String typeString) {
        return resolve(typeString, true);
    }

    private java.lang.reflect.Type resolve(String typeString, boolean shouldThrow) {
        StringTruncator parameterNameTruncator = new StringTruncator(typeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        java.lang.reflect.Type baseType = typeMap.get(baseParameterName);
        if (baseType == null) {
            if (!shouldThrow) {
                return null;
            }

            if (baseParameterName.equals(typeString)) {
                throw new PartialInterfaceException.NotCompletedException(
                        "cannot find type parameter: " + typeString // TODO: more detail
                );
            } else {
                 // TODO: test coverage
                throw new PartialInterfaceException.NotCompletedException(
                        "cannot find base type parameter: " + baseParameterName // TODO: more detail
                                + " for parameter: " + typeString
                );
            }
        }

        if (baseType instanceof Class) {
            Class<?> actualType = (Class<?>) baseType;
            while (arrayLevels > 0) {
                actualType = Array.newInstance(actualType, 0).getClass();
                arrayLevels--;
            }
            return actualType;
        }

        if (baseType instanceof ParameterizedType) {
            if (arrayLevels > 0) {
                throw new RuntimeException("unimplemented");
            }
            return baseType;
        }

        throw new RuntimeException("unimplemented");
    }

    @Override
    public String toString() {
        return typeParameterNames.stream()
                .collect(Collectors.toMap(x -> x, typeMap::get))
                .toString();
    }
}

