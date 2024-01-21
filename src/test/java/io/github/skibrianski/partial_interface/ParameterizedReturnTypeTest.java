package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedReturnTypeTest {

    @RequiresChildMethod(
            returnType = @Type(value = Type.TypeParameter.class, parameterName = "R"),
            argumentTypes = {},
            methodName = "method"
    )
    interface ReturnsLoneTypeParameter { }

    @HasTypeParameter(name = "R", value = int.class)
    public static class ValidWithExactTypeParameterMatch implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_valid_exactTypeMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithExactTypeParameterMatch.class));
    }

    public interface A { }
    public interface AChild extends A { }

    @HasTypeParameter(name = "R", value = A.class)
    public static class ValidWithChildOfTypeParameter implements ReturnsLoneTypeParameter {
        public AChild method() {
            return null;
        }
    }
    @Test
    void test_valid_childTypeMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildOfTypeParameter.class));
    }

    @HasTypeParameter(name = "R", value = int.class)
    public static class WrongReturnType implements ReturnsLoneTypeParameter {
        public String method() {
            return "three";
        }
    }
    @Test
    void test_wrongReturnType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongReturnType.class)
        );
    }
}