package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleTypeVariableArgumentTypeTest {

    public static class A { }
    public static class AChild extends A { }

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface HasMethodWithLoneTypeParameter { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class ValidClassPrimitiveMatch implements HasMethodWithLoneTypeParameter {
        public void method(int foo) { }
    }
    @Test
    void test_valid_primitive() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClassPrimitiveMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = A.class)
    static class ValidClassExactObjectMatch implements HasMethodWithLoneTypeParameter {
        public void method(A foo) { }
    }
    @Test
    void test_valid_exactObjectMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClassExactObjectMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = A.class)
    public static class ValidClassChildObjectTypeMatch implements HasMethodWithLoneTypeParameter {
        public void method(AChild foo) { }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidClassChildObjectTypeMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class WrongArgumentType implements HasMethodWithLoneTypeParameter {
        public void method(String foo) { }
    }
    @Test
    void test_invalid_doesNotMatchTypeParameter() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(WrongArgumentType.class)
        );
    }

}