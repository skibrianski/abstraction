package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CompoundParameterizedArgumentWithMultipleArgumentsTypeTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter("U")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("Map<T, U>")},
            methodName = "method"
    )
    interface WithCompoundParameterizedArgumentType { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class Valid implements WithCompoundParameterizedArgumentType {
        public void method(Map<String, Integer> foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }


    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongBaseType implements WithCompoundParameterizedArgumentType {
        public void method(Map.Entry<String, Integer> foo) { }
    }
    @Test
    void test_invalid_wrongBaseType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(WrongBaseType.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongFirstParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Map<Integer, Integer> foo) { }
    }
    @Test
    void test_invalid_wrongFirstParameterType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(WrongFirstParameterType.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongSecondParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Map<String, String> foo) { }
    }
    @Test
    void test_invalid_wrongSecondParameterType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(WrongSecondParameterType.class)
        );
    }

}