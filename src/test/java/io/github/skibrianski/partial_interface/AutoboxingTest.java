package io.github.skibrianski.partial_interface;

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

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    public static class Valid implements WithSum {
        public int sum(List<Integer> input) {
            return input.stream().reduce(0, (a, b) -> a + b);
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = double.class)
    public static class Invalid implements WithSum {
        public int sum(List<Integer> input) {
            return input.stream().reduce(0, (a, b) -> a + b);
        }
    }
    @Test
    void test_invalid_wrongTypes() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }

}