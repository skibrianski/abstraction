package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterfaceExtendingInterfaceTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {@Type(ofClass = String.class)},
            methodName = "scramble"
    )
    interface WithScrambler { }

    @ManualValidation
    public interface ValidFull extends WithScrambler {
        String scramble(String input);
    }
    @Test
    void test_valid_fulfilled() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidFull.class));
    }

    @ManualValidation
    public interface ValidEmpty extends WithScrambler {
        String scramble(String input);
    }
    @Test
    void test_valid_unfulfilled() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidEmpty.class));
    }

    @ManualValidation
    public interface InvalidInterface extends WithScrambler {
        int scramble(String input);
    }
    @Test
    void test_invalid_returnTypeClash() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(InvalidInterface.class)
        );
    }
}