package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleParameterizedArgumentTypesTest {

    @RequiresChildMethod(
            returnType = @Type(void.class),
            argumentTypes = {
                    @Type(value = Type.TypeParameter.class, parameterName = "T"),
                    @Type(value = Type.TypeParameter.class, parameterName = "X")
            },
            methodName = "method"
    )
    interface HasMethodWithMultipleTypeParameters { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", value = int.class)
    @HasTypeParameter(name = "X", value = String.class)
    static class Valid implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, String bar) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    public static class A { }
    public static class AChild extends A { }
    public static class B { }
    public static class BChild extends B { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", value = A.class)
    @HasTypeParameter(name = "X", value = B.class)
    static class ChildReturnTypes implements HasMethodWithMultipleTypeParameters {
        public void method(AChild foo, BChild bar) { }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", value = int.class)
    @HasTypeParameter(name = "X", value = String.class)
    static class FirstParameterIsIncorrect implements HasMethodWithMultipleTypeParameters {
        public void method(String String, int bar) { }
    }
    @Test
    void test_invalid_firstParameterIsIncorrect() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(FirstParameterIsIncorrect.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", value = int.class)
    @HasTypeParameter(name = "X", value = String.class)
    static class SecondParameterIsIncorrect implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, int bar) { }
    }
    @Test
    void test_invalid_secondParameterIsIncorrect() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(SecondParameterIsIncorrect.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", value = int.class)
    @HasTypeParameter(name = "X", value = String.class)
    static class ExtraParameter implements HasMethodWithMultipleTypeParameters {
        public void method(int foo, String bar, boolean extra) { }
    }
    @Test
    void test_invalid_extraParameter() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(ExtraParameter.class)
        );
    }
}