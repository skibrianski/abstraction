package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveTest {

    @RequiresChildMethod(
            returnType = @Type(int.class),
            argumentTypes = {@Type(int.class)},
            methodName = "triple"
    )
    interface WithTriple { }

    @PartialInterfaceWithManualValidation
    public static class Valid implements WithTriple {
        public int triple(int input) {
            return input * 3;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @PartialInterfaceWithManualValidation
    public static class Invalid implements WithTriple { }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }
}