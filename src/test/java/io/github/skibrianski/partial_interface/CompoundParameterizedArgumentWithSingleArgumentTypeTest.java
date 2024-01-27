package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CompoundParameterizedArgumentWithSingleArgumentTypeTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("Collection<T>")},
            methodName = "method"
    )
    interface WithCompoundParameterizedArgumentType { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class ValidExactMatch implements WithCompoundParameterizedArgumentType {
        public void method(Collection<String> foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class ValidExtendedBaseType implements WithCompoundParameterizedArgumentType {
        public void method(List<String> foo) { }
    }
    @Test
    void test_valid_extendedBaseType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExtendedBaseType.class));
    }


    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = String.class)
    static class WrongBaseType implements WithCompoundParameterizedArgumentType {
        public void method(AtomicReference<String> foo) { }
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
    static class WrongParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Collection<Integer> foo) { }
    }
    @Test
    void test_invalid_wrongParameterType() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(WrongParameterType.class)
        );
    }

}