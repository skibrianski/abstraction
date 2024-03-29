package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractClassOnAbstractClassTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    abstract static class WithScrambler { }

    @ManualValidation
    public abstract static class ValidFull extends WithScrambler {
        public abstract String scramble(String input);
    }
    @Test
    void test_valid_fulfilled() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidFull.class));
    }

    @ManualValidation
    public abstract static class ValidMatchingOverride extends WithScrambler {
        public abstract String scramble(String input);
    }
    @Test
    void test_valid_unfulfilled_matchingAbstractMethod() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidMatchingOverride.class));
    }

    @ManualValidation
    public abstract static class ValidEmpty extends WithScrambler {
    }
    @Test
    void test_valid_unfulfilled_noMethod() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidEmpty.class));
    }

    @ManualValidation
    public abstract static class InvalidClashingReturnOnConcreteMethod extends WithScrambler {
        public int scramble(String input) {
            return 3;
        }
    }
    @Test
    void test_invalid_clashingReturnTypeOnConcreteMethod() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(InvalidClashingReturnOnConcreteMethod.class)
        );
    }

    @ManualValidation
    public abstract static class InvalidClashingReturnOnAbstractMethod extends WithScrambler {
        public abstract int scramble(String input);
    }
    @Test
    void test_invalid_clashingReturnTypeOnAbstractMethod() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(InvalidClashingReturnOnAbstractMethod.class)
        );
    }
}