package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.Map;

public class TypeNameResolver {

    private final Map<String, Class<?>> scalarTypeParameterMap;

    public TypeNameResolver(Map<String, Class<?>> scalarTypeParameterMap) {
        this.scalarTypeParameterMap = scalarTypeParameterMap;
    }

    public boolean isEmpty() {
        return scalarTypeParameterMap.isEmpty();
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

        Class<?> baseType = scalarTypeParameterMap.get(baseParameterName);
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

        Class<?> actualType = baseType;
        while (arrayLevels > 0) {
            actualType = Array.newInstance(actualType, 0).getClass();
            arrayLevels--;
        }
        return actualType;
    }

    @Override
    public String toString() {
        return scalarTypeParameterMap.toString();
    }
}

