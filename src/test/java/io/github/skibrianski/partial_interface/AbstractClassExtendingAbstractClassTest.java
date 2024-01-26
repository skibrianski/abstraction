package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractClassExtendingAbstractClassTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    abstract static class WithScrambler { }

    @PartialInterfaceWithManualValidation
    public abstract static class ValidFull extends WithScrambler {
        public abstract String scramble(String input);
    }
    @Test
    void test_valid_fulfilled() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidFull.class));
    }

    @PartialInterfaceWithManualValidation
    public abstract static class ValidMatchingOverride extends WithScrambler {
        public abstract String scramble(String input);
    }
    @Test
    void test_valid_unfulfilled_matchingAbstractMethod() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidMatchingOverride.class));
    }

    @PartialInterfaceWithManualValidation
    public abstract static class ValidEmpty extends WithScrambler {
    }
    @Test
    void test_valid_unfulfilled_noMethod() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidEmpty.class));
    }

    @PartialInterfaceWithManualValidation
    public abstract static class InvalidClashingReturnOnConcreteMethod extends WithScrambler {
        public int scramble(String input) {
            return 3;
        }
    }
    @Test
    void test_invalid_clashingReturnTypeOnConcreteMethod() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(InvalidClashingReturnOnConcreteMethod.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public abstract static class InvalidClashingReturnOnAbstractMethod extends WithScrambler {
        public abstract int scramble(String input);
    }
    @Test
    void test_invalid_clashingReturnTypeOnAbstractMethod() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(InvalidClashingReturnOnAbstractMethod.class)
        );
    }
}