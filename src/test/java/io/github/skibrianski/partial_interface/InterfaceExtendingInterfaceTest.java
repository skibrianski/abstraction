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

    @PartialInterfaceWithManualValidation
    public interface ValidInterface extends WithScrambler {
        String scramble(String input1);
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterface.class));
    }

    @PartialInterfaceWithManualValidation
    public interface InvalidInterface extends WithScrambler { }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(InvalidInterface.class)
        );
    }
}