package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardComplexTest {

    public interface AGrandParent { }
    public interface AParent extends AGrandParent { }
    public interface BParent { }
    public interface Self extends AParent, BParent { }
    public interface AOnlySelf extends AParent { }
    public interface AChild extends Self { }
    public interface BChild extends Self { }

    @RequiresTypeParameter(
            value = "T",
            extending = {"AGrandParent", "BParent"},
            superOf = {"AChild"}
    )
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Self.class)
    static class Valid implements WithMethod {
        public void method(Self thing) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = AOnlySelf.class)
    static class InvalidFailsLowerBound implements WithMethod {
        public void method(AOnlySelf thing) { }
    }
    @Test
    void test_invalid_failsLowerBound() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidFailsLowerBound.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = BChild.class)
    static class InvalidFailsUpperBound implements WithMethod {
        public void method(BChild thing) { }
    }
    @Test
    void test_invalid_failsUpperBound() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidFailsUpperBound.class)
        );
    }
}