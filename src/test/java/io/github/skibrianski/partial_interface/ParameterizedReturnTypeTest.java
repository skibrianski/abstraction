package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedReturnTypeTest {

    @RequiresChildMethod(
            returnType = @Type("R"),
            argumentTypes = {},
            methodName = "method"
    )
    interface ReturnsLoneTypeParameter { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
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

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = A.class)
    public static class ValidWithChildOfTypeParameter implements ReturnsLoneTypeParameter {
        public AChild method() {
            return null;
        }
    }
    @Test
    void test_valid_childTypeMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidWithChildOfTypeParameter.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
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

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "X", ofClass = int.class)
    public static class WrongTypeParameterName implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_invalidTypeParameterName() {
        // TODO: should validate HasTypeParameter and throw a precise exception on that instead.
        Assertions.assertThrows(
                PartialInterfaceUsageException.class,
                () -> PartialInterface.check(WrongTypeParameterName.class)
        );
    }
}