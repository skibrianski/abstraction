package io.github.skibrianski.partial_interface.internal;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.util.Map;

public class TypeVariable extends IType {

    // handles eg @Type("R"), @Type("Z"), @Type("CONTAINER")

    @Override
    public Class<?> getActualType() {
        StringTruncator parameterNameTruncator = new StringTruncator(typeString)
                .truncateOnce("...")
                .truncateAll("[]");
        String baseParameterName = parameterNameTruncator.value();
        int arrayLevels = parameterNameTruncator.truncationCount();

        Class<?> baseType = getTypeParameter(baseParameterName);
        if (baseType == null) {
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

    private final String typeString;

    public TypeVariable(String typeString, Map<String, Class<?>> typeParameterMap) {
        super(typeParameterMap);
        this.typeString = typeString;
    }

    public String name() {
        return this.typeString;
    }
}
