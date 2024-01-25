package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = int.class),
            argumentTypes = {@Type(ofClass = int.class)},
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
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }
}