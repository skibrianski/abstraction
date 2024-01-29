package io.github.skibrianski.abstraction;

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
                AbstractionException.UsageException.class,
                () -> Abstraction.check(MatchesCorrectly.class)
        );
    }

}