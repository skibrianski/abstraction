//package io.github.skibrianski.partial_interface;
//
//import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class InterfaceTest {
//
//    @RequiresChildMethod(
//            returnType = @Type(String.class),
//            argumentTypes = {@Type(String.class)},
//            methodName = "scramble"
//    )
//    interface WithScrambler { }
//
//    public interface ValidInterface extends WithScrambler {
//        String scramble(String input1);
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterface.class));
//    }
//
//    public interface InvalidInterface extends WithScrambler { }
//    @Test
//    void test_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceNotCompletedException.class,
//                () -> PartialInterface.check(InvalidInterface.class)
//        );
//    }
//}