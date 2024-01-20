package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleParameterizedArgumentTypeTest {

    public static class A { }
    public static class AChild extends A { }

    @RequiresTypeParameters(count = 1)
    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(void.class),
            argumentTypes = {
                    @RequiresChildMethod.Type(
                            value = RequiresChildMethod.FirstParameter.class,
                            type = RequiresChildMethod.TypeType.PARAMETERIZED
                    )
            },
            methodName = "method"
    )
    interface HasMethodWithLoneTypeParameter { }

    @HasTypeParameters(int.class)
    static class ValidClassPrimitiveMatch implements HasMethodWithLoneTypeParameter {
        public void method(int foo) { }
    }
    @Test
    void test_valid_primitive() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassPrimitiveMatch.class));
    }

    @HasTypeParameters(A.class)
    static class ValidClassExactObjectMatch implements HasMethodWithLoneTypeParameter {
        public void method(A foo) { }
    }
    @Test
    void test_valid_exactObjectMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassExactObjectMatch.class));
    }

    @HasTypeParameters(A.class)
    public static class ValidClassChildObjectTypeMatch implements HasMethodWithLoneTypeParameter {
        public void method(AChild foo) { }
    }
    @Test
    void test_valid_childReturnType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassChildObjectTypeMatch.class));
    }


    @HasTypeParameters(int.class)
    static class WrongArgumentType implements HasMethodWithLoneTypeParameter {
        public void method(String foo) { }
    }
    @Test
    void test_invalid_doesNotMatchTypeParameter() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongArgumentType.class)
        );
    }

}