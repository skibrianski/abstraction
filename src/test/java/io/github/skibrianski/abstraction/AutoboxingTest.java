package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AutoboxingTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("List<T>")},
            methodName = "sum"
    )
    interface WithSum { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    public static class Valid implements WithSum {
        public int sum(List<Integer> input) {
            return input.stream().reduce(0, Integer::sum);
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = double.class)
    public static class Invalid implements WithSum {
        public int sum(List<Integer> input) {
            return input.stream().reduce(0, Integer::sum);
        }
    }
    @Test
    void test_invalid_wrongTypes() {
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }

}