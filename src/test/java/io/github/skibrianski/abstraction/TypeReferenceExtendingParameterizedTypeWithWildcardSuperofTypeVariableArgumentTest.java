package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceExtendingParameterizedTypeWithWildcardSuperofTypeVariableArgumentTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter(value = "U", extending = "Collection<? super T>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("U")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    @HasTypeParameter(name = "U", ofString = "List<Number>")
    static class ValidSubclass implements WithMethod {
        public void method(Integer t, List<Number> u) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    @HasTypeParameter(name = "U", ofString = "List<String>")
    static class InvalidWrongTypeParameter implements WithMethod {
        public void method(Integer t, List<String> u) { }
    }
    @Test
    void test_invalid_wrongScalarType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidWrongTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    @HasTypeParameter(name = "U", ofString = "AtomicReference<Number>")
    static class InvalidWrongRawType implements WithMethod {
        public void method(Integer t, AtomicReference<Number> u) { }
    }
    @Test
    void test_invalid_wrongRawType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(InvalidWrongRawType.class)
        );
    }

}