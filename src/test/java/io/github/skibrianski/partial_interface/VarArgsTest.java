//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class VarArgsTest {
//
//    @RequiresChildMethod(
//            returnType = @Type(ofClass = String.class),
//            argumentTypes = {@Type(ofClass = String[].class)},
//            methodName = "concat"
//    )
//    interface WithConcatenation { }
//
//    @PartialInterfaceWithManualValidation
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
//    @PartialInterfaceWithManualValidation
//    public static class InvalidClass implements WithConcatenation { }
//    @Test
//    void test_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(InvalidClass.class)
//        );
//    }
//}