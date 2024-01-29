package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleTypeVariableUseTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T"), @Type("T")},
            methodName = "method"
    )
    interface TheAbstraction { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class Valid implements TheAbstraction {
        public int method(int foo, int bar) {
            return -1;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadReturnType implements TheAbstraction {
        public void method(int foo, int bar) { }
    }
    @Test
    void test_invalid_badReturnType() {
        Assertions.assertThrows(
                AbstractionException.ClashingReturnTypeException.class,
                () -> Abstraction.check(BadReturnType.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadArgument1Type implements TheAbstraction {
        public int method(String foo, int bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badFirstArgumentType() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(BadArgument1Type.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadArgument2Type implements TheAbstraction {
        public int method(int foo, boolean bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badSecondArgumentType() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(BadArgument2Type.class)
        );
    }
}