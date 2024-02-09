package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceToAnotherTypeReferenceAsWildcardExtendsMultipleTypeParameterTest {

    @RequiresTypeParameter(value = "T", extending = "Serializable & Comparable<T>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    static class Valid implements WithMethod {
        public void method(Integer value) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    // TODO
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = Number.class)
//    @HasTypeParameter(name = "U", ofString = "List<String>")
//    static class InvalidWrongTypeParameter implements WithMethod {
//        public void method(Number t, List<String> u) { }
//    }
//    @Test
//    void test_invalid_wrongScalarType() {
//        Assertions.assertThrows(
//                AbstractionException.TypeParameterViolatesBoundsException.class,
//                () -> Abstraction.check(InvalidWrongTypeParameter.class)
//        );
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = Number.class)
//    @HasTypeParameter(name = "U", ofString = "AtomicReference<Integer>")
//    static class InvalidWrongRawType implements WithMethod {
//        public void method(Number t, AtomicReference<Integer> u) { }
//    }
//    @Test
//    void test_invalid_wrongRawType() {
//        Assertions.assertThrows(
//                AbstractionException.TypeParameterViolatesBoundsException.class,
//                () -> Abstraction.check(InvalidWrongRawType.class)
//        );
//    }

}