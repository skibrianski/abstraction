package io.github.skibrianski.abstraction;

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

    @ManualValidation
    public static class ValidClassExactMatch implements WithScrambler {
        public A scramble(A input) {
            return input;
        }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClassExactMatch.class));
    }

    @ManualValidation
    public static class ValidWithChildReturnType implements WithScrambler {
        public AChild scramble(A input) {
            return null;
        }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidWithChildReturnType.class));
    }

    @ManualValidation
    public static class ValidWithChildParameterType implements WithScrambler {
        public A scramble(AChild input) {
            return null;
        }
    }
    @Test
    void test_valid_childParameterType() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidWithChildParameterType.class));
    }

    @ManualValidation
    public static class NoMethodClass implements WithScrambler { }
    @Test
    void test_invalid_noMethod() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(NoMethodClass.class)
        );
    }

    @ManualValidation
    public static class WrongReturnTypeClass implements WithScrambler {
        public int scramble(A input) {
            return 3;
        }
    }
    @Test
    void test_invalid_wrongReturnType() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(WrongReturnTypeClass.class)
        );
    }

    @ManualValidation
    public static class MissingArgumentClass implements WithScrambler {
        public A scramble() {
            return new A();
        }
    }
    @Test
    void test_invalid_missingArgument() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(MissingArgumentClass.class)
        );
    }

    @ManualValidation
    public static class ExtraArgumentClass implements WithScrambler {
        public A scramble(A input, int extraArg) {
            return input;
        }
    }
    @Test
    void test_invalid_extraArgument() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(ExtraArgumentClass.class)
        );
    }

    @ManualValidation
    public static class WrongArgumentTypeClass implements WithScrambler {
        public A scramble(int input) {
            return new A();
        }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(WrongArgumentTypeClass.class)
        );
    }
}