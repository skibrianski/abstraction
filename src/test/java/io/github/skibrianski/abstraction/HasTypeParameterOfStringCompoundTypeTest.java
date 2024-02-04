package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class HasTypeParameterOfStringCompoundTypeTest {

    @RequiresTypeParameter("T")
    @RequiresChildMethod(
            returnType = @Type("T"),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofString = "List<String>")
    public static class Valid implements WithMethod {
        public List<String> method(List<String> input) {
            return input;
        }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofString = "List<Integer>")
    public static class InvalidWrongTypeParameter implements WithMethod {
        public List<String> method(List<String> input) {
            return input;
        }
    }
    @Test
    void test_invalid_wrongTypeParameter() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(InvalidWrongTypeParameter.class)
        );
    }

//    // TODO
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofString = "List<Integer>")
//    public static class InvalidWrongTypeParameter2 implements WithMethod {
//        public List<Integer> method(List<Integer> input) {
//            return input;
//        }
//    }
//    @Test
//    void test_invalid_wrongTypeParameter2() {
//        Assertions.assertThrows(
//                AbstractionException.ClashingArgumentTypeException.class,
//                () -> Abstraction.check(InvalidWrongTypeParameter2.class)
//        );
//    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofString = "List<Integer>")
    public static class InvalidWrongBaseType implements WithMethod {
        public Set<String> method(Set<String> input) {
            return input;
        }
    }
    @Test
    void test_invalid_wrongBaseType() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(InvalidWrongBaseType.class)
        );
    }

//    // TODO
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofString = "Set<Integer>")
//    public static class InvalidWrongBaseType2 implements WithMethod {
//        public Set<Integer> method(Set<Integer> input) {
//            return input;
//        }
//    }
//    @Test
//    void test_invalid_wrongBaseType2() {
//        Assertions.assertThrows(
//                AbstractionException.ClashingArgumentTypeException.class,
//                () -> Abstraction.check(InvalidWrongBaseType2.class)
//        );
//    }

}