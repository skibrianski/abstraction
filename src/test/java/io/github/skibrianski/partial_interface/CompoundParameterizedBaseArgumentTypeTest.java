package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CompoundParameterizedBaseArgumentTypeTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T<String>")},
            methodName = "method"
    )
    interface WithCompoundParameterizedArgumentType { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = Set.class)
    static class ValidExactMatch implements WithCompoundParameterizedArgumentType {
        public void method(Set<String> foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = Collection.class)
    static class ValidExtendedBaseType implements WithCompoundParameterizedArgumentType {
        public void method(Set<String> foo) { }
    }
    @Test
    void test_valid_extendedBaseType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExtendedBaseType.class));
    }


    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = Set.class)
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
    @HasTypeParameter(name = "T", ofClass = Set.class)
    static class WrongParameterType implements WithCompoundParameterizedArgumentType {
        public void method(Set<Integer> foo) { }
    }
    @Test
    void test_invalid_wrongParameterType() {
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> PartialInterface.check(WrongParameterType.class)
        );
    }

}