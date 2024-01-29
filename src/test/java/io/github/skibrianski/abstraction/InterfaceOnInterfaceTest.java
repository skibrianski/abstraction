package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterfaceOnInterfaceTest {

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
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidFull.class));
    }

    @ManualValidation
    public interface ValidEmpty extends WithScrambler {
    }
    @Test
    void test_valid_unfulfilled() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidEmpty.class));
    }

    @ManualValidation
    public interface InvalidInterface extends WithScrambler {
        int scramble(String input);
    }
    @Test
    void test_invalid_returnTypeClash() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(InvalidInterface.class)
        );
    }
}