package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveTest {

    @PartialInterface(returnType = int.class, argumentTypes = {int.class}, methodName = "triple")
    interface WithTriple { }

    public interface InvalidInterface extends WithTriple { }

    public interface ValidInterface extends WithTriple {
        int triple(int input);
    }

    public static class InvalidClass implements WithTriple { }

    public static class ValidClass implements WithTriple {
        public int triple(int input) {
            return input * 3;
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