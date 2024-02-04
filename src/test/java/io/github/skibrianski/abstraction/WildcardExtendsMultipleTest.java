package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardExtendsMultipleTest {

    @RequiresTypeParameter(value = "T", extending = {"Serializable", "Comparable"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class ValidSubClass implements WithMethod {
        public void method(String foo) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubClass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class) // Serializable but not Comparable
    static class Invalid implements WithMethod {
        public void method(Number foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }

}