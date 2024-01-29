package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassOnAbstractClassTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    static abstract class WithScrambler { }

    @ManualValidation
    public static class ValidFull extends WithScrambler {
        public String scramble(String input) {
            return "hi";
        }
    }
    @Test
    void test_valid_fulfilled() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidFull.class));
    }

    @ManualValidation
    public static class Empty extends WithScrambler {
    }
    @Test
    void test_invalid_unfulfilled() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(Empty.class)
        );
    }

    @ManualValidation
    public static class Invalid extends WithScrambler {
        public int scramble(String input) {
            return 3;
        }
    }
    @Test
    void test_invalid_returnTypeClash() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }
}