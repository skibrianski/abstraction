package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterizedReturnTypeTest {

    @RequiresTypeParameter("R")
    @RequiresChildMethod(
            returnType = @Type("R"),
            argumentTypes = {},
            methodName = "method"
    )
    interface ReturnsLoneTypeParameter { }

    @ManualValidation
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

    @ManualValidation
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

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    public static class WrongReturnType implements ReturnsLoneTypeParameter {
        public String method() {
            return "three";
        }
    }
    @Test
    void test_wrongReturnType() {
        Assertions.assertThrows(
                PartialInterfaceException.ClashingReturnTypeException.class,
                () -> PartialInterface.check(WrongReturnType.class)
        );
    }

    @ManualValidation
    public static class MissingTypeParameter implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_missingTypeParameterName() {
        Assertions.assertThrows(
                PartialInterfaceException.MissingTypeParameterException.class,
                () -> PartialInterface.check(MissingTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "R", ofClass = int.class)
    @HasTypeParameter(name = "X", ofClass = int.class)
    public static class ExtraneousTypeParameter implements ReturnsLoneTypeParameter {
        public int method() {
            return 3;
        }
    }
    @Test
    void test_invalidTypeParameterName() {
        Assertions.assertThrows(
                PartialInterfaceException.ExtraneousTypeParameterException.class,
                () -> PartialInterface.check(ExtraneousTypeParameter.class)
        );
    }
}