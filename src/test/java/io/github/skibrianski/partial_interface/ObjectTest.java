package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectTest {

    @PartialInterface(returnType = String.class, argumentTypes = {String.class}, methodName = "scramble")
    interface WithScrambler { }

    public interface InvalidInterface extends WithScrambler { }

    public interface ValidInterface extends WithScrambler {
        String scramble(String input1);
    }

    public static class InvalidClass implements WithScrambler { }

    public static class ValidClass implements WithScrambler {
        public String scramble(String input1) {
            return input1;
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