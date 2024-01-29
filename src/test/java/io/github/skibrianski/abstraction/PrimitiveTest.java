package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = int.class),
            argumentTypes = {@Type(ofClass = int.class)},
            methodName = "triple"
    )
    interface WithTriple { }

    @ManualValidation
    public static class Valid implements WithTriple {
        public int triple(int input) {
            return input * 3;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    public static class InvalidWrongReturnType implements WithTriple {
        public String triple(int input) {
            return "bob";
        }
    }
    @Test
    void test_invalid_wrongReturnType() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(InvalidWrongReturnType.class)
        );
    }

    @ManualValidation
    public static class InvalidEmpty implements WithTriple { }
    @Test
    void test_invalid_missingMethod() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(InvalidEmpty.class)
        );
    }
}