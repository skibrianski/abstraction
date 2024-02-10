package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceExtendingParameterizedTypeWithMultipleTypeVariableArgumentTest {

    @RequiresTypeParameter("K")
    @RequiresTypeParameter("V")
    @RequiresTypeParameter(value = "M", extending = "Map<K, V>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("M")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "K", ofClass = String.class)
    @HasTypeParameter(name = "V", ofClass = Number.class)
    @HasTypeParameter(name = "M", ofString = "Map<String, Number>")
    static class ValidSubclass implements WithMethod {
        public void method(Map<String, Number> map) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "K", ofClass = String.class)
    @HasTypeParameter(name = "V", ofClass = Number.class)
    @HasTypeParameter(name = "M", ofString = "Map<Number, Number>")
    static class InvalidWrongFirstTypeParameter implements WithMethod {
        public void method(Map<Number, Number> map) { }
    }
    @Test
    void test_invalid_wrongFirstTypeParameter() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidWrongFirstTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "K", ofClass = String.class)
    @HasTypeParameter(name = "V", ofClass = Number.class)
    @HasTypeParameter(name = "M", ofString = "Map<String, String>")
    static class InvalidWrongSecondTypeParameter implements WithMethod {
        public void method(Map<String, String> map) { }
    }
    @Test
    void test_invalid_wrongSecondTypeParameter() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidWrongSecondTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "K", ofClass = String.class)
    @HasTypeParameter(name = "V", ofClass = Number.class)
    @HasTypeParameter(name = "M", ofString = "Map.Entry<String, Number>")
    static class InvalidWrongRawType implements WithMethod {
        public void method(Map.Entry<String, String> map) { }
    }
    @Test
    void test_invalid_wrongRawType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidWrongRawType.class)
        );
    }

}