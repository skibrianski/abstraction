package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardComplexTest {

    public interface AParent { }
    public interface AChild extends AParent { }

    // TODO: holy yuck.
    @RequiresTypeParameter(value = "T", lowerBound = {"io.github.skibrianski.partial_interface.WildcardComplexTest$AParent"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = AChild.class)
    static class Valid implements WithMethod {
        public void method(AChild thing) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(Valid.class));
    }

//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = LocalTime.class)
//    static class ValidExactMatch implements WithMethod {
//        public void method(LocalTime temporal) { }
//    }
//    @Test
//    void test_valid_exactMatch() {
//        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidExactMatch.class));
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = String.class)
//    static class Invalid implements WithMethod {
//        public void method(String temporal) { }
//    }
//    @Test
//    void test_invalid_violatedTypeConstraint() {
//        Assertions.assertThrows(
//                PartialInterfaceException.TypeParameterViolatesBounds.class,
//                () -> PartialInterface.check(Invalid.class)
//        );
//    }

}