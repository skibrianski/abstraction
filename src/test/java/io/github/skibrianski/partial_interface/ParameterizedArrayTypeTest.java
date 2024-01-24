package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedArrayTypeTest {

    @RequiresChildMethod(
            returnType = @Type("R"),
            argumentTypes = {@Type("R...")},
            methodName = "sum"
    )
    interface WithSummation { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class Valid implements WithSummation {
        public int sum(int... addends) {
            return 3;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int[].class)
    public static class MultiDimensionalArray implements WithSummation {
        public int[] sum(int[]... addends) {
            return new int[]{3};
        }
    }
    @Test
    void test_valid_multiDimensionalArray() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(MultiDimensionalArray.class));
    }

    public static class A { }
    public static class AChild extends A { }
    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = A.class)
    public static class ChildType implements WithSummation {
        public A sum(AChild... addends) {
            return new A();
        }
    }
    @Test
    void test_valid_arrayOfChildType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ChildType.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class NotArray implements WithSummation {
        public int sum(int addends) {
            return 3;
        }
    }
    @Test
    void test_invalid_notArray() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(NotArray.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class WrongArrayType implements WithSummation {
        public int sum(String... addends) {
            return 3;
        }
    }
    @Test
    void test_invalid_wrongArrayType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongArrayType.class)
        );
    }
}