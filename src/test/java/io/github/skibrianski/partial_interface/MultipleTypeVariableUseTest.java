package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleTypeVariableUseTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T"), @Type("T")},
            methodName = "method"
    )
    interface ThePartialInterface { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class Valid implements ThePartialInterface {
        public int method(int foo, int bar) {
            return -1;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadReturnType implements ThePartialInterface {
        public void method(int foo, int bar) { }
    }
    @Test
    void test_invalid_badReturnType() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(BadReturnType.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadArgument1Type implements ThePartialInterface {
        public int method(String foo, int bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badFirstArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(BadArgument1Type.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class BadArgument2Type implements ThePartialInterface {
        public int method(int foo, boolean bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badSecondArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(BadArgument2Type.class)
        );
    }
}