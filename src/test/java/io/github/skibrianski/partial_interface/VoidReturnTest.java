package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VoidReturnTest {

    @RequiresChildMethod(
            returnType = @Type(void.class),
            argumentTypes = {},
            methodName = "doIt"
    )
    interface WithDoer { }

    @ValidatePartialInterfaceManually
    public static class Doer implements WithDoer {
        public void doIt() { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Doer.class));
    }

}