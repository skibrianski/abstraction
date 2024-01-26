//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class AutomaticValidationInvocationTest {
//
//
//    @RequiresChildMethod(
//            returnType = @Type(ofClass = int.class),
//            argumentTypes = {@Type(ofClass = int.class)},
//            methodName = "method"
//    )
//    interface WithScrambler { }
//
//    // note: NOT annotated with @ValidatePartialInterfaceManually
//    public static class ValidClassExactMatch implements WithScrambler {
//        public int method(int input) {
//            return input;
//        }
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidClassExactMatch.class));
//    }
//}