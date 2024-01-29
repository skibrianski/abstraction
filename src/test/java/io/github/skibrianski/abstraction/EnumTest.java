package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumTest {

    public enum Alpha {
        A, B, C;
    }

    @RequiresTypeParameter(value = "T", lowerBound = "Enum<T>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Alpha.class)
    public static class Valid implements WithMethod {
        public void method(Alpha input) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    public static class Invalid implements WithMethod {
        public void method(String input) { }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(Invalid.class)
        );
    }
}