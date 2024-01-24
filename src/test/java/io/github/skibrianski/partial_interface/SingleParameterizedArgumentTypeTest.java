package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SingleParameterizedArgumentTypeTest {


    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("List<String>")},
            methodName = "method"
    )
    interface Valid { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofString = "List<String>")
    static class ValidClassPrimitiveMatch implements Valid {
        public void method(List<String> foo) { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassPrimitiveMatch.class));
    }

}