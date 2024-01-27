//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Set;
//
//public class HasTypeParameterOfStringCompoundTypeTest {
//
//    @RequiresTypeParameter("T")
//    @RequiresChildMethod(
//            returnType = @Type("T"),
//            argumentTypes = {@Type("T")},
//            methodName = "method"
//    )
//    interface WithMethod { }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofString = "List<String>")
//    public static class Valid implements WithMethod {
//        public List<String> method(List<String> input) {
//            return input;
//        }
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofString = "List<Integer>")
//    public static class InvalidWrongTypeParameter implements WithMethod {
//        public List<String> method(List<String> input) {
//            return input;
//        }
//    }
//    @Test
//    void test_invalid_wrongTypeParameter() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(InvalidWrongTypeParameter.class)
//        );
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofString = "List<Integer>")
//    public static class InvalidWrongBaseType implements WithMethod {
//        public Set<String> method(Set<String> input) {
//            return input;
//        }
//    }
//    @Test
//    void test_invalid_wrongBaseType() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(InvalidWrongBaseType.class)
//        );
//    }
//
//}