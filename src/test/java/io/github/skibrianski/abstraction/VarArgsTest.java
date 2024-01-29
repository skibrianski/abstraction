package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VarArgsTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String[].class)},
            methodName = "concat"
    )
    interface WithConcatenation { }

    @ManualValidation
    public static class ValidClass implements WithConcatenation {
        public String concat(String... inputs) {
            return String.join("", inputs);
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClass.class));
    }

    @ManualValidation
    public static class InvalidClass implements WithConcatenation { }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(InvalidClass.class)
        );
    }
}