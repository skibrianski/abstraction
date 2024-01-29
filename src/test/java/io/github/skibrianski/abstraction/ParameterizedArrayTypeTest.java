package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedArrayTypeTest {

    @RequiresTypeParameter("R")
    @RequiresChildMethod(
            returnType = @Type("R"),
            argumentTypes = {@Type("R...")},
            methodName = "sum"
    )
    interface WithSummation { }

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class Valid implements WithSummation {
        public int sum(int... addends) {
            return 3;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int[].class)
    public static class MultiDimensionalArray implements WithSummation {
        public int[] sum(int[]... addends) {
            return new int[]{3};
        }
    }
    @Test
    void test_valid_multiDimensionalArray() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(MultiDimensionalArray.class));
    }

    public static class A { }
    public static class AChild extends A { }
    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = A.class)
    public static class ChildType implements WithSummation {
        public A sum(AChild... addends) {
            return new A();
        }
    }
    @Test
    void test_valid_arrayOfChildType() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ChildType.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class NotArray implements WithSummation {
        public int sum(int addends) {
            return 3;
        }
    }
    @Test
    void test_invalid_notArray() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(NotArray.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class WrongArrayType implements WithSummation {
        public int sum(String... addends) {
            return 3;
        }
    }
    @Test
    void test_invalid_wrongArrayType() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(WrongArrayType.class)
        );
    }
}