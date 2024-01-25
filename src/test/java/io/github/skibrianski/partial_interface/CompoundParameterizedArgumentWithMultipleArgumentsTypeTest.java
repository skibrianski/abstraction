package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CompoundParameterizedArgumentWithMultipleArgumentsTypeTest {

    @RequiresTypeParameter({"T", "U"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("Map<T, U>")},
            methodName = "method"
    )
    interface WithCompoundParameterizedArgumentType { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class Valid implements WithCompoundParameterizedArgumentType {
        public void method(Map<String, Integer> foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }


    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongBaseType implements WithCompoundParameterizedArgumentType {
        public void method(Map.Entry<String, Integer> foo) { }
    }
    @Test
    void test_invalid_wrongBaseType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongBaseType.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongFirstParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Map<Integer, Integer> foo) { }
    }
    @Test
    void test_invalid_wrongFirstParameterType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongFirstParameterType.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    @HasTypeParameter(name = "U", ofClass = Integer.class)
    static class WrongSecondParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Map<String, String> foo) { }
    }
    @Test
    void test_invalid_wrongSecondParameterType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongSecondParameterType.class)
        );
    }

}