package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VoidReturnTest {

    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {},
            methodName = "doIt"
    )
    interface WithDoer { }

    @ManualValidation
    public static class Doer implements WithDoer {
        public void doIt() { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Doer.class));
    }

}