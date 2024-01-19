package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VarArgsTest {

    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(String.class),
            argumentTypes = {String[].class},
            methodName = "concat"
    )
    interface WithConcatenation { }

    public interface InvalidInterface extends WithConcatenation { }

    public interface ValidInterface extends WithConcatenation {
        String concat(String... inputs);
    }

    public static class InvalidClass implements WithConcatenation { }

    public static class ValidClass implements WithConcatenation {
        public String concat(String... inputs) {
            return String.join("", inputs);
        }
    }

    @Test
    void test_class_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
    }

    @Test
    void test_class_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(InvalidClass.class)
        );
    }

    @Test
    void test_interface_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterface.class));
    }

    @Test
    void test_interface_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(InvalidInterface.class)
        );
    }
}