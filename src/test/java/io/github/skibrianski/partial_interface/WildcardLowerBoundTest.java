package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardLowerBoundTest {

    @RequiresTypeParameter(value = "T", lowerBound = {"Number"})
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T"), @Type("T")},
            methodName = "sum"
    )
    interface WithSum { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    static class ValidSubClass implements WithSum {
        public Integer sum(Integer foo, Integer bar) {
            return -1;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidSubClass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    static class ValidExactMatch implements WithSum {
        public Number sum(Number foo, Number bar) {
            return -1;
        }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class Invalid implements WithSum {
        public String sum(String foo, String bar) {
            return "";
        }
    }
    @Test
    void test_invalid_violatedTypeConstraint() {
        Assertions.assertThrows(
                PartialInterfaceException.TypeParameterViolatesBounds.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }

}