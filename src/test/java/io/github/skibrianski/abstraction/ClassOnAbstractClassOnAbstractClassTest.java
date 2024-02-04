package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassOnAbstractClassOnAbstractClassTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    abstract static class WithScrambler { }

    @ManualValidation
    public abstract static class EmptyIntermediate extends WithScrambler {
    }
    @ManualValidation
    public static class FullChildOfEmptyIntermediate extends EmptyIntermediate {
        public String scramble(String input) {
            return "gibberish";
        }
    }
    @Test
    void test_valid_withEmptyIntermediate() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(EmptyIntermediate.class));
        Assertions.assertDoesNotThrow(() -> Abstraction.check(FullChildOfEmptyIntermediate.class));
    }

    @ManualValidation
    public static class EmptyChildOfEmptyIntermediate extends EmptyIntermediate {
    }
    @Test
    void test_invalid_withEmptyIntermediateAndEmptyChild() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(EmptyIntermediate.class));
        Assertions.assertThrows(
                AbstractionException.NoMethodWithMatchingName.class,
                () -> Abstraction.check(EmptyChildOfEmptyIntermediate.class)
        );
    }

    @ManualValidation
    public static class WrongReturnTypeChildOfEmptyIntermediate extends EmptyIntermediate {
        public int scramble(String input) {
            return 3;
        }
    }
    @Test
    void test_invalid_withEmptyIntermediateAndWrongReturnTypeChild() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(EmptyIntermediate.class));
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(WrongReturnTypeChildOfEmptyIntermediate.class)
        );
    }

    @ManualValidation
    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble2"
    )
    public abstract static class IntermediateWithRequirements extends WithScrambler {
    }
    @ManualValidation
    public static class ValidTwoRequirements extends IntermediateWithRequirements {
        public String scramble(String input) {
            return "gibberish";
        }

        public String scramble2(String input) {
            return "poppycock";
        }
    }
    @Test
    void test_valid_withNonEmptyIntermediate() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(IntermediateWithRequirements.class));
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidTwoRequirements.class));
    }

    @ManualValidation
    public static class InvalidMissingFirstOfTwoRequirements extends IntermediateWithRequirements {
        public String scramble2(String input) {
            return "poppycock";
        }
    }
    @Test
    void test_invalid_missingFirstOfTwoRequirements() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(IntermediateWithRequirements.class));
        Assertions.assertThrows(
                AbstractionException.NoMethodWithMatchingName.class,
                () -> Abstraction.check(InvalidMissingFirstOfTwoRequirements.class)
        );
    }

    @ManualValidation
    public static class InvalidMissingSecondOfTwoRequirements extends IntermediateWithRequirements {
        public String scramble(String input) {
            return "gibberish";
        }
    }
    @Test
    void test_invalid_missingSecondOfTwoRequirements() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(IntermediateWithRequirements.class));
        Assertions.assertThrows(
                AbstractionException.NoMethodWithMatchingName.class,
                () -> Abstraction.check(InvalidMissingSecondOfTwoRequirements.class)
        );
    }

}