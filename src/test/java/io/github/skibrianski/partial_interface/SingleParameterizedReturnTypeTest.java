package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleParameterizedReturnTypeTest {

    // TODO: return type can be child of typeParam, eg `LocalDate foo()` satisfies `Temporal foo()`
    @RequiresTypeParameters(count = 1)
    @RequiresChildMethod(
            returnType = @RequiresChildMethod.Type(
                    value = RequiresChildMethod.FirstParameter.class,
                    type = RequiresChildMethod.TypeType.PARAMETERIZED
            ),
            argumentTypes = {},
            methodName = "method"
    )
    interface ReturnsLoneTypeParameter { }

    @HasTypeParameters({int.class})
    public static class ValidWithExactTypeParameterMatch implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithExactTypeParameterMatch.class));
    }

    public interface A { }
    public interface AChild extends A { }

    @HasTypeParameters({A.class})
    public static class ValidWithChildOfTypeParameter implements ReturnsLoneTypeParameter {
        public AChild method() {
            return null;
        }
    }
    // TODO: this is true whether its a type param or not. fix up ObjectTest, maybe others.
    @Test
    void test_valid2() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildOfTypeParameter.class));
    }

    @HasTypeParameters({int.class})
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