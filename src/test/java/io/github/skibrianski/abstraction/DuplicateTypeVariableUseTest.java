package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DuplicateTypeVariableUseTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T"), @Type("T")},
            methodName = "method"
    )
    interface TheInterface { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class Valid implements TheInterface {
        public int method(int foo, int bar) {
            return -1;
        }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertThrows(
                AbstractionException.UsageException.class,
                () -> Abstraction.check(Valid.class)
        );
    }

}