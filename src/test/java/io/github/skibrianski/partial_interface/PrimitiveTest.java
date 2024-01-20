package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveTest {

    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(int.class),
            argumentTypes = {int.class},
            methodName = "triple"
    )
    interface WithTriple { }


    public static class ValidClass implements WithTriple {
        public int triple(int input) {
            return input * 3;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
    }

    public static class InvalidClass implements WithTriple { }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(InvalidClass.class)
        );
    }
}