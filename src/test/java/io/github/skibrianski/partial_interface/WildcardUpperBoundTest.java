package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public class WildcardUpperBoundTest {

    @RequiresTypeParameter(value = "T", upperBound = {"LocalTime"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Temporal.class)
    static class ValidSuperClass implements WithMethod {
        public void method(Temporal temporal) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidSuperClass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = LocalTime.class)
    static class ValidExactMatch implements WithMethod {
        public void method(LocalTime temporal) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class Invalid implements WithMethod {
        public void method(String temporal) { }
    }
    @Test
    void test_invalid_violatedTypeConstraint() {
        Assertions.assertThrows(
                PartialInterfaceException.TypeParameterViolatesBounds.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }

}