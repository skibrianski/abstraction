package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongClassNameTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = java.lang.String.class)
    public static class Valid implements WithMethod {
        public void method(java.lang.String input) { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    interface String { }
    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = java.lang.String.class)
    public static class Invalid implements WithMethod {
        public void method(String input) { }
    }
    @Test
    void test_invalid_wrongPackage() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }
}