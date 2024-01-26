//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class AbstractClassExtendingAbstractClassTest {
//
//    @RequiresChildMethod(
//            returnType = @Type(ofClass = String.class),
//            argumentTypes = {@Type(ofClass = String.class)},
//            methodName = "scramble"
//    )
//    abstract static class WithScrambler { }
//
//    @PartialInterfaceWithManualValidation
//    public abstract static class Valid extends WithScrambler {
//        public abstract String scramble(String input);
//    }
//    @Test
//    void test_valid() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
//    }
//
//    @PartialInterfaceWithManualValidation
//    public abstract static class Invalid extends WithScrambler {
//    }
//
//    @Test
//    void test_invalid() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(Invalid.class)
//        );
//    }
//}