package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleParameterUseTest {

    @RequiresChildMethod(
            returnType = @Type(value = Type.TypeParameter.class, parameterName = "T"),
            argumentTypes = {
                    @Type(value = Type.TypeParameter.class, parameterName = "T"),
                    @Type(value = Type.TypeParameter.class, parameterName = "T")
            },
            methodName = "method"
    )
    interface ThePartialInterface { }

    @HasTypeParameter(name = "T", value = int.class)
    static class Valid implements ThePartialInterface {
        public int method(int foo, int bar) {
            return -1;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @HasTypeParameter(name = "T", value = int.class)
    static class BadReturnType implements ThePartialInterface {
        public void method(int foo, int bar) { }
    }
    @Test
    void test_invalid_badReturnType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(BadReturnType.class)
        );
    }

    @HasTypeParameter(name = "T", value = int.class)
    static class BadArgument1Type implements ThePartialInterface {
        public int method(String foo, int bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badFirstArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(BadArgument1Type.class)
        );
    }

    @HasTypeParameter(name = "T", value = int.class)
    static class BadArgument2Type implements ThePartialInterface {
        public int method(int foo, boolean bar) {
            return 3;
        }
    }
    @Test
    void test_invalid_badSecondArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(BadArgument2Type.class)
        );
    }
}