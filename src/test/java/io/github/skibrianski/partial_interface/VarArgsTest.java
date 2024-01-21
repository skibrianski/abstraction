//package io.github.skibrianski.partial_interface;
//
//import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class VarArgsTest {
//
//    @RequiresChildMethod(
//            returnType = @Type(String.class),
//            argumentTypes = {@Type(String[].class)},
//            methodName = "concat"
//    )
//    interface WithConcatenation { }
//
//    public static class ValidClass implements WithConcatenation {
//        public String concat(String... inputs) {
//            return String.join("", inputs);
//        }
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClass.class));
//    }
//
//    public static class InvalidClass implements WithConcatenation { }
//    @Test
//    void test_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceNotCompletedException.class,
//                () -> PartialInterface.check(InvalidClass.class)
//        );
//    }
//}