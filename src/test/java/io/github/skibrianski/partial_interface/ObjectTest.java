package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectTest {

    public static class A { }
    public static class AChild extends A { }

    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(A.class),
            argumentTypes = {@RequiresChildMethod.Type(A.class)},
            methodName = "scramble"
    )
    interface WithScrambler { }

    public static class ValidClassExactMatch implements WithScrambler {
        public A scramble(A input1) {
            return input1;
        }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassExactMatch.class));
    }

    public static class ValidWithChildReturnType implements WithScrambler {
        public AChild scramble(A input1) {
            return null;
        }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildReturnType.class));
    }

    public static class ValidWithChildParameterType implements WithScrambler {
        public A scramble(AChild input1) {
            return null;
        }
    }
    @Test
    void test_valid_childParameterType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildParameterType.class));
    }

    public static class NoMethodClass implements WithScrambler { }
    @Test
    void test_invalid_noMethod() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(NoMethodClass.class)
        );
    }

    public static class WrongReturnTypeClass implements WithScrambler {
        public int scramble(A input1) {
            return 3;
        }
    }
    @Test
    void test_invalid_wrongReturnType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongReturnTypeClass.class)
        );
    }

    public static class MissingArgumentClass implements WithScrambler {
        public A scramble() {
            return new A();
        }
    }
    @Test
    void test_invalid_missingArgument() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(MissingArgumentClass.class)
        );
    }

    public static class ExtraArgumentClass implements WithScrambler {
        public A scramble(A input, int extraArg) {
            return input;
        }
    }
    @Test
    void test_invalid_extraArgument() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(ExtraArgumentClass.class)
        );
    }

    public static class WrongArgumentTypeClass implements WithScrambler {
        public A scramble(int input) {
            return new A();
        }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongArgumentTypeClass.class)
        );
    }
}