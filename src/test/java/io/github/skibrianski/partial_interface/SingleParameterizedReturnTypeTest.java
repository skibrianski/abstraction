package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleParameterizedReturnTypeTest {

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
    public interface ValidInterface extends ReturnsLoneTypeParameter {
        int method();
    }
    @Test
    void test_interface_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterface.class));
    }

//    // TODO: lots more invalid cases
//    public interface InvalidInterface extends ReturnsLoneTypeParameter { }
//    @Test
//    void test_interface_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceNotCompletedException.class,
//                () -> PartialInterface.check(InvalidInterface.class)
//        );
//    }

//    public static class InvalidClass implements ReturnsLoneTypeParameter { }
//
//    public static class ValidClass implements ReturnsLoneTypeParameter {
//        public String scramble(String input1) {
//            return input1;
//        }
//    }
//
//    @Test
//    void test_class_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
//    }
//
//    @Test
//    void test_class_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceNotCompletedException.class,
//                () -> PartialInterface.check(InvalidClass.class)
//        );
//    }

}