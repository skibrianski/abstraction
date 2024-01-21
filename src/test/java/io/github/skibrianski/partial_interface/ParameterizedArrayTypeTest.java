package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedArrayTypeTest {

    @RequiresChildMethod(
            returnType = @Type(value = Type.TypeParameter.class, parameterName = "R"),
            argumentTypes = {@Type(value = Type.TypeParameter.class, parameterName = "R...")},
            methodName = "sum"
    )
    interface ReturnsLoneTypeParameter { }

    @HasTypeParameter(name = "R", value = int.class)
    public static class Valid implements ReturnsLoneTypeParameter {
        public int sum(int... addends) {
            return 3;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }
}