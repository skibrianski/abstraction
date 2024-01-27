package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConcreteClassWithAnnotationTest {

    @ManualValidation
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {},
            methodName = "method"
    )
    public static class Invalid { }

    @ManualValidation
    static class MatchesCorrectly extends Invalid {
        public void method() { }
    }
    @Test
    void test_invalid_exactMatch_butAttemptingToExtendConcreteClass() {
        Assertions.assertThrows(
                PartialInterfaceException.UsageException.class,
                () -> PartialInterface.check(MatchesCorrectly.class)
        );
    }

}