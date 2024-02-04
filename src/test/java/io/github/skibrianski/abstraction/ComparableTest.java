package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComparableTest {

    public static class ComparableThing implements Comparable<ComparableThing> {
        @Override
        public int compareTo(ComparableThing o) {
            return 0;
        }
    }


    @RequiresTypeParameter(value = "T", extending = "Comparable<T>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = ComparableThing.class)
    public static class Valid implements WithMethod {
        public void method(ComparableThing input) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    public static class NonComparable { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = NonComparable.class)
    public static class Invalid implements WithMethod {
        public void method(NonComparable input) { }
    }
    @Test
    void test_invalid_wrongArgumentType() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }

}