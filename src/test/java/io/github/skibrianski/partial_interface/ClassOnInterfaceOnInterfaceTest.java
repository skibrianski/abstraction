package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassOnInterfaceOnInterfaceTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    interface WithScrambler { }

    @ManualValidation
    interface EmptyIntermediate extends WithScrambler {
    }
    @ManualValidation
    public static class FullChildOfEmptyIntermediate implements EmptyIntermediate {
        public String scramble(String input) {
            return "gibberish";
        }
    }
    @Test
    void test_valid_withEmptyIntermediate() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(EmptyIntermediate.class));
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(FullChildOfEmptyIntermediate.class));
    }

    @ManualValidation
    public static class EmptyChildOfEmptyIntermediate implements EmptyIntermediate {
    }
    @Test
    void test_invalid_withEmptyIntermediateAndEmptyChild() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(EmptyIntermediate.class));
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(EmptyChildOfEmptyIntermediate.class)
        );
    }

    @ManualValidation
    public static class WrongReturnTypeChildOfEmptyIntermediate implements EmptyIntermediate {
        public int scramble(String input) {
            return 3;
        }
    }
    @Test
    void test_invalid_withEmptyIntermediateAndWrongReturnTypeChild() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(EmptyIntermediate.class));
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(WrongReturnTypeChildOfEmptyIntermediate.class)
        );
    }

    @ManualValidation
    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble2"
    )
    interface IntermediateWithRequirements extends WithScrambler {
    }
    @ManualValidation
    public static class ValidTwoRequirements implements IntermediateWithRequirements {
        public String scramble(String input) {
            return "gibberish";
        }

        public String scramble2(String input) {
            return "poppycock";
        }
    }
    @Test
    void test_valid_withNonEmptyIntermediate() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(IntermediateWithRequirements.class));
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidTwoRequirements.class));
    }

    @ManualValidation
    public static class InvalidMissingFirstOfTwoRequirements implements IntermediateWithRequirements {
        public String scramble2(String input) {
            return "poppycock";
        }
    }
    @Test
    void test_invalid_missingFirstOfTwoRequirements() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(IntermediateWithRequirements.class));
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(InvalidMissingFirstOfTwoRequirements.class)
        );
    }

    @ManualValidation
    public static class InvalidMissingSecondOfTwoRequirements implements IntermediateWithRequirements {
        public String scramble(String input) {
            return "gibberish";
        }
    }
    @Test
    void test_invalid_missingSecondOfTwoRequirements() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(IntermediateWithRequirements.class));
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(InvalidMissingSecondOfTwoRequirements.class)
        );
    }

}