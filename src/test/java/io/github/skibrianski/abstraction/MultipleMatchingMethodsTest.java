package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleMatchingMethodsTest {

    @RequiresTypeParameter(value = "T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @HasTypeParameter(name = "T", ofString = "? extends Number")
    @ManualValidation
    public static class Valid implements WithMethod {
        public Integer method(Integer input) { return 3; }
        public Double method(Double input) { return 3.; }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @HasTypeParameter(name = "T", ofString = "? extends Number")
    @ManualValidation
    public static class ValidWithNearMatch implements WithMethod {
        public String method(String input) { return "three"; } // does not fulfill contract, but should not cause error
        public Double method(Double input) { return 3.; } // fulfills contract
    }
    @Test
    void test_valid_skipsNearMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidWithNearMatch.class));
    }

}