//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class CompoundArgumentTypeTest {
//
//
//    @RequiresTypeParameter("T")
//    @RequiresChildMethod(
//            returnType = @Type(ofClass = void.class),
//            argumentTypes = {@Type("T"), @Type("Collection<String>")},
//            methodName = "method"
//    )
//    interface WithCompoundArgumentType { }
//
//    @PartialInterfaceWithManualValidation
//    @HasTypeParameter(name = "T", ofClass = int.class)
//    static class ValidExactMatch implements WithCompoundArgumentType {
//        public void method(int foo, Collection<String> bar) { }
//    }
//    @Test
//    void test_valid_exactMatch() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
//    }
//
//    @PartialInterfaceWithManualValidation
//    @HasTypeParameter(name = "T", ofClass = int.class)
//    static class ValidExtendedBaseType implements WithCompoundArgumentType {
//        public void method(int foo, List<String> bar) { }
//    }
//    @Test
//    void test_valid_extendedBaseType() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExtendedBaseType.class));
//    }
//
//    @PartialInterfaceWithManualValidation
//    @HasTypeParameter(name = "T", ofClass = int.class)
//    static class WrongBaseType implements WithCompoundArgumentType {
//        public void method(int foo, AtomicReference<String> bar) { }
//    }
//    @Test
//    void test_invalid_wrongBaseType() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(WrongBaseType.class)
//        );
//    }
//
//    @PartialInterfaceWithManualValidation
//    @HasTypeParameter(name = "T", ofClass = int.class)
//    static class WrongParameterType implements WithCompoundArgumentType {
//        public void method(int foo, Collection<Integer> bar) { }
//    }
//    @Test
//    void test_invalid_wrongParameterType() {
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> PartialInterface.check(WrongParameterType.class)
//        );
//    }
//
//}