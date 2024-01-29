package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceToAnotherTypeReferenceAsArrayTypeParameterTest {
    @RequiresTypeParameter("T")
    @RequiresTypeParameter(value = "U", lowerBound = "Collection<T[]>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("U")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofString = "List<String[]>")
    static class ValidSubclass implements WithMethod {
        public void method(String t, List<String[]> u) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofString = "List<Integer>")
    static class InvalidNotArray implements WithMethod {
        public void method(String t, List<Integer> u) { }
    }
    @Test
    void test_invalid_notArray() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidNotArray.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofString = "List<Integer[][]>")
    static class InvalidWrongArrayDimension implements WithMethod {
        public void method(String t, List<Integer[][]> u) { }
    }
    @Test
    void test_invalid_wrongArrayDimension() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongArrayDimension.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofString = "List<Integer[]>")
    static class InvalidWrongTypeParameter implements WithMethod {
        public void method(String t, List<Integer[]> u) { }
    }
    @Test
    void test_invalid_wrongScalarType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofString = "AtomicReference<String[]>")
    static class InvalidWrongRawType implements WithMethod {
        public void method(String t, AtomicReference<String[]> u) { }
    }
    @Test
    void test_invalid_wrongRawType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongRawType.class)
        );
    }

}