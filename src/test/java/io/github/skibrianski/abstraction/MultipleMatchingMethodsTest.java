package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleMatchingMethodsTest {

    @RequiresTypeParameter(value = "T")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @HasTypeParameter(name = "T", ofString = "? extends Number")
    @ManualValidation
    public static class Valid implements WithMethod {
        public void method(Integer input) { }
        public void method(Double input) { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

//    @ManualValidation
//    public static class InvalidWrongReturnType implements WithTriple {
//        public String triple(int input) {
//            return "bob";
//        }
//    }
//    @Test
//    void test_invalid_wrongReturnType() {
//        Assertions.assertThrows(
//                AbstractionException.ClashingReturnTypeException.class,
//                () -> Abstraction.check(InvalidWrongReturnType.class)
//        );
//    }
//
//    @ManualValidation
//    public static class InvalidEmpty implements WithTriple { }
//    @Test
//    void test_invalid_missingMethod() {
//        Assertions.assertThrows(
//                AbstractionException.NoMethodWithMatchingName.class,
//                () -> Abstraction.check(InvalidEmpty.class)
//        );
//    }
}