package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectTest {

    public static class A { }
    public static class AChild extends A { }

    @RequiresChildMethod(
            returnType = @Type(ofClass = A.class),
            argumentTypes = {@Type(ofClass = A.class)},
            methodName = "scramble"
    )
    interface WithScrambler { }

    @PartialInterfaceWithManualValidation
    public static class ValidClassExactMatch implements WithScrambler {
        public A scramble(A input) {
            return input;
        }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassExactMatch.class));
    }

    @PartialInterfaceWithManualValidation
    public static class ValidWithChildReturnType implements WithScrambler {
        public AChild scramble(A input) {
            return null;
        }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildReturnType.class));
    }

    @PartialInterfaceWithManualValidation
    public static class ValidWithChildParameterType implements WithScrambler {
        public A scramble(AChild input) {
            return null;
        }
    }
    @Test
    void test_valid_childParameterType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildParameterType.class));
    }

    @PartialInterfaceWithManualValidation
    public static class NoMethodClass implements WithScrambler { }
    @Test
    void test_invalid_noMethod() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(NoMethodClass.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public static class WrongReturnTypeClass implements WithScrambler {
        public int scramble(A input) {
            return 3;
        }
    }
    @Test
    void test_invalid_wrongReturnType() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(WrongReturnTypeClass.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public static class MissingArgumentClass implements WithScrambler {
        public A scramble() {
            return new A();
        }
    }
    @Test
    void test_invalid_missingArgument() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(MissingArgumentClass.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public static class ExtraArgumentClass implements WithScrambler {
        public A scramble(A input, int extraArg) {
            return input;
        }
    }
    @Test
    void test_invalid_extraArgument() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(ExtraArgumentClass.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public static class WrongArgumentTypeClass implements WithScrambler {
        public A scramble(int input) {
            return new A();
        }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(WrongArgumentTypeClass.class)
        );
    }
}