package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConcreteClassExtendingAbstractClassTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    abstract static class WithScrambler { }

    @ManualValidation
    public static class Valid extends WithScrambler {
        public String scramble(String input) {
            return input;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    public static class Invalid extends WithScrambler {
    }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }
}