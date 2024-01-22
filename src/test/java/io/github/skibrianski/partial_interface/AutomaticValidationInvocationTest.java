package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AutomaticValidationInvocationTest {


    @RequiresChildMethod(
            returnType = @Type(type = int.class),
            argumentTypes = {@Type(type = int.class)},
            methodName = "method"
    )
    interface WithScrambler { }

    // note: NOT annotated with @ValidatePartialInterfaceManually
    public static class ValidClassExactMatch implements WithScrambler {
        public int method(int input1) {
            return input1;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassExactMatch.class));
    }
}