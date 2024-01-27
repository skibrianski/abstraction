package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TypeNameResolver {

    private final Map<String, java.lang.reflect.Type> builtinTypeMap;
    private final Map<String, java.lang.reflect.Type> typeParameterMap;

    public TypeNameResolver() {
        this.builtinTypeMap = BuiltInTypeNameResolver.builtInClassMap();
        this.typeParameterMap = new HashMap<>();
    }

    @Deprecated
    public TypeNameResolver addAll(Map<String, java.lang.reflect.Type> typeParameterMap) {
        builtinTypeMap.putAll(typeParameterMap);
        return this;
    }

    public TypeNameResolver addTypeParameter(String name, java.lang.reflect.Type type) {
        builtinTypeMap.put(name, type);
        typeParameterMap.put(name, type);
        return this;
    }

    public boolean isEmpty() {
        return typeParameterMap.isEmpty();
    }

    public boolean canResolve(String typeString) {
        return resolve(typeString, false) != null;
    }

    // returns null on failure to lookup
    public Class<?> resolve(String typeString) {
        return resolve(typeString, false);
    }

    // throws Exception on failure to lookup
    public Class<?> mustResolve(String typeString) {
        return resolve(typeString, true);
    }

    private Class<?> resolve(String typeString, boolean shouldThrow) {
        StringTruncator parameterNameTruncator = new StringTruncator(typeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        java.lang.reflect.Type baseType = builtinTypeMap.get(baseParameterName);
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

        throw new RuntimeException("unimplemented");
    }

    @Override
    public String toString() {
        return typeParameterMap.toString();
    }
}

