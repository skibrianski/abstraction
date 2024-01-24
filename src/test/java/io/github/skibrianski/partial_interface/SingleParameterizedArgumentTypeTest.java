package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleParameterizedArgumentTypeTest {


    @RequiresChildMethod(
            returnType = @Type(byClass = void.class),
            argumentTypes = {@Type("List<String>")},
            methodName = "method"
    )
    interface Valid { }
//
//    @PartialInterfaceWithManualValidation
//    @HasTypeParameter(name = "T", value = int.class)
//    static class ValidClassPrimitiveMatch implements Valid {
//        public void method(int foo) { }
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassPrimitiveMatch.class));
//    }

}