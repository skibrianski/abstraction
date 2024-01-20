package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleParameterizedReturnTypeTest {

    @RequiresTypeParameters(count = 1)
    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(
                    value = RequiresChildMethod.FirstParameter.class,
                    type = RequiresChildMethod.TypeType.PARAMETERIZED
            ),
            argumentTypes = {},
            methodName = "method"
    )
    interface ReturnsLoneTypeParameter { }

    @HasTypeParameters({int.class})
    public static class Valid implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }
}