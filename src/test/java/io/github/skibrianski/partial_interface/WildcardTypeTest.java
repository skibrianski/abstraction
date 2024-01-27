//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class WildcardTypeTest {
//
//    @RequiresTypeParameter(value = "T", extending = "Number")
//    @RequiresChildMethod(
//            returnType = @Type("T"),
//            argumentTypes = {@Type("T"), @Type("T")},
//            methodName = "sum"
//    )
//    interface WithSum { }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = Integer.class)
//    static class Valid implements WithSum {
//        public Integer sum(Integer foo, Integer bar) {
//            return -1;
//        }
//    }
//    @Test
//    void test_valid_happyPath() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = String.class)
//    static class Invalid implements WithSum {
//        public String sum(String foo, String bar) {
//            return "";
//        }
//    }
//    @Test
//    void test_invalid_violatedTypeConstraint() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(Invalid.class)
//        );
//    }
//
//}