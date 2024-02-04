package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleTypeVariableArgumentTypesTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter("X")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("X")},
            methodName = "method"
    )
    interface HasMethodWithMultipleTypeParameters { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    @HasTypeParameter(name = "X", ofClass = String.class)
    static class Valid implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, String bar) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    public static class A { }
    public static class AChild extends A { }
    public static class B { }
    public static class BChild extends B { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = A.class)
    @HasTypeParameter(name = "X", ofClass = B.class)
    static class ChildReturnTypes implements HasMethodWithMultipleTypeParameters {
        public void method(AChild foo, BChild bar) { }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    @HasTypeParameter(name = "X", ofClass = String.class)
    static class FirstParameterIsIncorrect implements HasMethodWithMultipleTypeParameters {
        public void method(String String, int bar) { }
    }
    @Test
    void test_invalid_firstParameterIsIncorrect() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(FirstParameterIsIncorrect.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    @HasTypeParameter(name = "X", ofClass = String.class)
    static class SecondParameterIsIncorrect implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, int bar) { }
    }
    @Test
    void test_invalid_secondParameterIsIncorrect() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(SecondParameterIsIncorrect.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    @HasTypeParameter(name = "X", ofClass = String.class)
    static class ExtraParameter implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, String bar, boolean extra) { }
    }
    @Test
    void test_invalid_extraParameter() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(ExtraParameter.class)
        );
    }
}