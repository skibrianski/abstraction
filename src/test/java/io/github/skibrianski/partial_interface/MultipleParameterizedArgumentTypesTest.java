package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleParameterizedArgumentTypesTest {

    @RequiresTypeParameters(count = 2)
    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(void.class),
            argumentTypes = {
                    @RequiresChildMethod.Type(
                            value = RequiresChildMethod.FirstParameter.class,
                            type = RequiresChildMethod.TypeType.PARAMETERIZED
                    ),
                    @RequiresChildMethod.Type(
                            value = RequiresChildMethod.SecondParameter.class,
                            type = RequiresChildMethod.TypeType.PARAMETERIZED
                    )
            },
            methodName = "method"
    )
    interface HasMethodWithMultipleTypeParameters { }

    @HasTypeParameters({int.class, String.class})
    static class Valid implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, String bar) { }
    }
    @Test
    void test_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

}