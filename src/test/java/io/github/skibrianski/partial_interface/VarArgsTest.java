package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VarArgsTest {

//    @PartialInterface(returnType = String.class, varArgType = int.class, methodName = "concat")
    interface WithConcatenation { }

    public interface InvalidInterface extends WithConcatenation { }

    public interface ValidInterface extends WithConcatenation {
        String concat(String... inputs);
    }

    public static class InvalidClass implements WithConcatenation { }

    public static class ValidClass implements WithConcatenation {
        public String concat(String input1, String input2) {
            return input1.concat(input2);
        }
    }

    @Test
    void test_class_valid() {
        Assertions.assertDoesNotThrow(() -> Checker.check(ValidClass.class));
    }

    @Test
    void test_class_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(InvalidClass.class)
        );
    }

    @Test
    void test_interface_valid() {
        Assertions.assertDoesNotThrow(() -> Checker.check(ValidInterface.class));
    }

    @Test
    void test_interface_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(InvalidInterface.class)
        );
    }
}