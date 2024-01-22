package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

public class TypeParameterMap {

    private final Map<String, Class<?>> scalarTypeParameterMap;

    public TypeParameterMap(Map<String, Class<?>> scalarTypeParameterMap) {
        this.scalarTypeParameterMap = scalarTypeParameterMap;
    }

    public boolean isEmpty() {
        return scalarTypeParameterMap.isEmpty();
    }

    @Deprecated
    public boolean containsKey(String typeString) {
        return scalarTypeParameterMap.containsKey(typeString);
    }

    public boolean canResolve(String typeString) {
        return resolve(typeString, false) != null;
    }

    public Class<?> resolve(String typeString) {
        return resolve(typeString, false);
    }

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
                throw new PartialInterfaceNotCompletedException(
                        "cannot find type parameter: " + typeString // TODO: more detail
                );
            } else {
                 // TODO: test coverage
                throw new PartialInterfaceNotCompletedException(
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
}

