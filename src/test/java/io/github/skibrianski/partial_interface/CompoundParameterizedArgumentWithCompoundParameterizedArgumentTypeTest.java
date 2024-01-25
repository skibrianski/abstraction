package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CompoundParameterizedArgumentWithCompoundParameterizedArgumentTypeTest {

    @RequiresTypeParameter({"T", "U"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T<U>")},
            methodName = "method"
    )
    interface WithCompoundParameterizedArgumentType { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = List.class)
    @HasTypeParameter(name = "U", ofClass = String.class)
    static class ValidExactMatch implements WithCompoundParameterizedArgumentType {
        public void method(List<String> foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = List.class)
    @HasTypeParameter(name = "U", ofClass = String.class)
    static class WrongBaseType implements WithCompoundParameterizedArgumentType {
        public void method(AtomicReference<String> foo) { }
    }
    @Test
    void test_invalid_wrongBaseType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongBaseType.class)
        );
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = List.class)
    @HasTypeParameter(name = "U", ofClass = String.class)
    static class WrongParameterType implements WithCompoundParameterizedArgumentType {
        public void method(List<Integer> foo) { }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongParameterType.class)
        );
    }


}