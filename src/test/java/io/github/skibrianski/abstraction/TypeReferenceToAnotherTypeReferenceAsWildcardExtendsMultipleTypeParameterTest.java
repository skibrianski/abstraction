package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
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

    public static class SerializableOnly implements Serializable {  }
    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = SerializableOnly.class)
    static class NotComparable implements WithMethod {
        public void method(SerializableOnly value) { }
    }
    @Test
    void test_invalid_notComparable() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(NotComparable.class)
        );
    }

    public static class ComparableOnly implements Comparable<ComparableOnly> {
        @Override
        public int compareTo(ComparableOnly o) {
            return 0;
        }
    }
    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = ComparableOnly.class)
    static class NotSerializable implements WithMethod {
        public void method(ComparableOnly value) { }
    }
    @Test
    void test_invalid_notSerializable() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(NotSerializable.class)
        );
    }




}