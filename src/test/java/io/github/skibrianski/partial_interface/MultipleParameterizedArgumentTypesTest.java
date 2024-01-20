package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
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
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    public static class A { }
    public static class AChild extends A { }
    public static class B { }
    public static class BChild extends B { }

    @HasTypeParameters({A.class, B.class})
    static class ChildReturnTypes implements HasMethodWithMultipleTypeParameters {
        public void method(AChild foo, BChild bar) { }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @HasTypeParameters({int.class, String.class})
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

    @HasTypeParameters({int.class, String.class})
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

    @HasTypeParameters({int.class, String.class})
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