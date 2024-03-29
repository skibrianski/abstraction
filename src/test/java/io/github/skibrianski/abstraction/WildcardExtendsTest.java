package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardExtendsTest {

    @RequiresTypeParameter(value = "T", extending = {"Number"})
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
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubClass.class));
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
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidExactMatch.class));
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
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }

}