package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceToAnotherTypeReferenceAsWildcardExtendsTypeParameterTest {

    @RequiresTypeParameter("T")
    @RequiresTypeParameter(value = "U", extending = "Collection<? extends T>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("U")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofString = "List<Integer>")
    static class ValidSubclass implements WithMethod {
        public void method(Number t, List<Integer> u) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofString = "List<String>")
    static class InvalidWrongTypeParameter implements WithMethod {
        public void method(Number t, List<String> u) { }
    }
    @Test
    void test_invalid_wrongScalarType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongTypeParameter.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Number.class)
    @HasTypeParameter(name = "U", ofString = "AtomicReference<Integer>")
    static class InvalidWrongRawType implements WithMethod {
        public void method(Number t, AtomicReference<Integer> u) { }
    }
    @Test
    void test_invalid_wrongRawType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBounds.class,
                () -> Abstraction.check(InvalidWrongRawType.class)
        );
    }

}