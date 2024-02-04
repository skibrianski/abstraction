package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepeatedAnnotationTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = int.class),
            argumentTypes = {},
            methodName = "intSupplier"
    )
    @RequiresChildMethod(
            returnType = @Type(ofClass = String.class),
            argumentTypes = {},
            methodName = "stringSupplier"
    )
    interface WithMultipleAnnotations { }

    @ManualValidation
    public static class ValidClass implements WithMultipleAnnotations {
        public int intSupplier() {
            return 3;
        }

        public String stringSupplier() {
            return "three";
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClass.class));
    }

    @ManualValidation
    public static class ClassWithoutIntSupplier implements WithMultipleAnnotations {
        public String stringSupplier() {
            return "three";
        }
    }
    @Test
    void test_invalid_noIntSupplier() {
        Assertions.assertThrows(
                AbstractionException.NoMethodWithMatchingName.class,
                () -> Abstraction.check(ClassWithoutIntSupplier.class)
        );
    }

    @ManualValidation
    public static class ClassWithoutStringSupplier implements WithMultipleAnnotations {
        public int intSupplier() {
            return 3;
        }
    }
    @Test
    void test_invalid_noStringSupplier() {
        Assertions.assertThrows(
                AbstractionException.NoMethodWithMatchingName.class,
                () -> Abstraction.check(ClassWithoutStringSupplier.class)
        );
    }
}