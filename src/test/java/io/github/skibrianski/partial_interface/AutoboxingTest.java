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
    interface WithScrambler { }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = int.class)
    interface Valid extends WithScrambler {
        int sum(List<Integer> input);
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

    @PartialInterfaceWithManualValidation
    @HasTypeParameter(name = "T", ofClass = double.class)
    interface Invalid extends WithScrambler {
        int sum(List<Integer> input);
    }
    @Test
    void test_invalid() {
        Assertions.assertThrows(
                PartialInterfaceException.NotCompletedException.class,
                () -> PartialInterface.check(Invalid.class)
        );
    }

}