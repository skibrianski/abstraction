package io.github.skibrianski.partial_interface;

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

    @PartialInterfaceWithManualValidation
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
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
    }

    @PartialInterfaceWithManualValidation
    public static class ClassWithoutIntSupplier implements WithMultipleAnnotations {
        public String stringSupplier() {
            return "three";
        }
    }
    @Test
    void test_invalid_noIntSupplier() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(ClassWithoutIntSupplier.class)
        );
    }

    @PartialInterfaceWithManualValidation
    public static class ClassWithoutStringSupplier implements WithMultipleAnnotations {
        public int intSupplier() {
            return 3;
        }
    }
    @Test
    void test_invalid_noStringSupplier() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(ClassWithoutStringSupplier.class)
        );
    }
}