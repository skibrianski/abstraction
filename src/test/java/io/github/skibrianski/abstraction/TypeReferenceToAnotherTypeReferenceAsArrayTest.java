package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeReferenceToAnotherTypeReferenceAsArrayTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter(value = "U", extending = "T[][]")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("U")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = String[][].class)
    static class ValidSubclass implements WithMethod {
        public void method(String t, String[][] u) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = String[][][].class)
    static class InvalidTooManyDimensions implements WithMethod {
        public void method(String t, String[][][] u) { }
    }
    @Test
    void test_invalid_tooManyDimension() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidTooManyDimensions.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = String[].class)
    static class InvalidTooFewDimensions implements WithMethod {
        public void method(String t, String[] u) { }
    }
    @Test
    void test_invalid_tooFewDimension() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidTooFewDimensions.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = int[][].class)
    static class InvalidWrongScalarType implements WithMethod {
        public void method(String t, int[][] u) { }
    }
    @Test
    void test_invalid_wrongScalarType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongScalarType.class)
        );
    }

}