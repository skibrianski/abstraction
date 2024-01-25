package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class CompoundTypeArgumentTest {


    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("Collection<String>")},
            methodName = "method"
    )
    interface WithParameterizedArgumentType { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class ValidExactMatch implements WithParameterizedArgumentType {
        public void method(int foo, Collection<String> bar) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    static class ValidExtendedBaseType implements WithParameterizedArgumentType {
        public void method(int foo, List<String> bar) { }
    }
    @Test
    void test_valid_extendedBaseType() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExtendedBaseType.class));
    }
}