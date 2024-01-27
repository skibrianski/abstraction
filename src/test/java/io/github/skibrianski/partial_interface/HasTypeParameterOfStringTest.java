package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HasTypeParameterOfStringTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithSum { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofString = "int")
    public static class Valid implements WithSum {
        public int method(int input) {
            return input;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofString = "double")
    public static class Invalid implements WithSum {
        public int method(int input) {
            return input;
        }
    }
    @Test
    void test_invalid_wrongTypes() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }

}