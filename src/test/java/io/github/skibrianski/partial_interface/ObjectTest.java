package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectTest {

    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(String.class),
            argumentTypes = {String.class},
            methodName = "scramble"
    )
    interface WithScrambler { }

    public static class ValidClass implements WithScrambler {
        public String scramble(String input1) {
            return input1;
        }
    }
    @Test
    void test_class_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
    }

    public static class NoMethodClass implements WithScrambler { }
    @Test
    void test_class_invalid_noMethod() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(NoMethodClass.class)
        );
    }

    public static class WrongReturnTypeClass implements WithScrambler {
        public int scramble(String input1) {
            return 3;
        }
    }
    @Test
    void test_class_invalid_wrongReturnType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongReturnTypeClass.class)
        );
    }

    public static class MissingArgumentClass implements WithScrambler {
        public String scramble() {
            return "gah";
        }
    }
    @Test
    void test_class_invalid_missingArgument() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(MissingArgumentClass.class)
        );
    }

    public static class ExtraArgumentClass implements WithScrambler {
        public String scramble(String input, int extraArg) {
            return input;
        }
    }
    @Test
    void test_class_invalid_extraArgument() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(ExtraArgumentClass.class)
        );
    }

    public static class WrongArgumentTypeClass implements WithScrambler {
        public String scramble(int input) {
            return "gah";
        }
    }
    @Test
    void test_class_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongArgumentTypeClass.class)
        );
    }

    public interface ValidInterface extends WithScrambler {
        String scramble(String input1);
    }
    @Test
    void test_interface_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterface.class));
    }

    public interface InvalidInterface extends WithScrambler { }
    @Test
    void test_interface_invalid() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(InvalidInterface.class)
        );
    }
}