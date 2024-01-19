package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepeatedAnnotationTest {

    @PartialInterface(returnType = int.class, argumentTypes = {}, methodName = "intSupplier")
    @PartialInterface(returnType = String.class, argumentTypes = {}, methodName = "stringSupplier")
    interface WithMultipleAnnotations { }

    public static class ValidClass implements WithMultipleAnnotations {
        public int intSupplier() {
            return 3;
        }

        public String stringSupplier() {
            return "three";
        }
    }
    @Test
    void test_class_valid() {
        Assertions.assertDoesNotThrow(() -> Checker.check(ValidClass.class));
    }

    public static class ClassWithoutIntSupplier implements WithMultipleAnnotations {
        public String stringSupplier() {
            return "three";
        }
    }
    @Test
    void test_class_invalid_noIntSupplier() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(ClassWithoutIntSupplier.class)
        );
    }

    public static class ClassWithoutStringSupplier implements WithMultipleAnnotations {
        public int intSupplier() {
            return 3;
        }
    }
    @Test
    void test_class_invalid_noStringSupplier() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(ClassWithoutStringSupplier.class)
        );
    }


    public interface ValidInterface extends WithMultipleAnnotations {
        int intSupplier();

        String stringSupplier();
    }
    @Test
    void test_interface_valid() {
        Assertions.assertDoesNotThrow(() -> Checker.check(ValidInterface.class));
    }


    public interface InterfaceWithoutIntSupplier extends WithMultipleAnnotations {
        String stringSupplier();
    }
    @Test
    void test_interface_invalid_noIntSupplier() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(InterfaceWithoutIntSupplier.class)
        );
    }

    public interface InterfaceWithoutStringSupplier extends WithMultipleAnnotations {
        int intSupplier();
    }
    @Test
    void test_interface_invalid_noStringSupplier() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> Checker.check(InterfaceWithoutStringSupplier.class)
        );
    }

}