package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeReferenceToAnotherTypeReferenceTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter(value = "U", extending = "T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("U")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class ValidSubclass implements WithMethod {
        public void method(Number t, Integer u) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofClass = Number.class)
    static class ValidExactMatch implements WithMethod {
        public void method(Number t, Number u) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidExactMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofClass = String.class)
    static class InvalidWrongU implements WithMethod {
        public void method(Number t, String u) { }
    }
    @Test
    void test_invalid_doesNotExtend() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongU.class)
        );
    }

}