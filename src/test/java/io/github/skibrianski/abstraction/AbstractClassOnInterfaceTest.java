package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractClassOnInterfaceTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    interface WithScrambler { }

    @ManualValidation
    public abstract static class ValidFull implements WithScrambler {
        public abstract String scramble(String input);
    }
    @Test
    void test_valid_fulfilled() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidFull.class));
    }

    @ManualValidation
    public abstract static class ValidEmpty implements WithScrambler {
    }
    @Test
    void test_valid_unfulfilled() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidEmpty.class));
    }

    @ManualValidation
    public abstract static class Invalid implements WithScrambler {
        public abstract int scramble(String input);
    }
    @Test
    void test_invalid_returnTypeClash() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }
}