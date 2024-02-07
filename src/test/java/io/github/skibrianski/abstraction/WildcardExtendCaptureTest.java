package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardExtendCaptureTest {

    @RequiresTypeParameter(value = "T", extending = {"Number"})
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T"), @Type("T")},
            methodName = "wat"
    )
    interface Foo { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    public static class Valid implements Foo {
        public void wat(Integer in1, Integer in2) { }
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(Valid.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    public static class InvalidBothUsesViolateCapture implements Foo {
        public void wat(Float in1, Float in2) { }
    }
    @Test
    void test_invalid_bothArgumentsViolateCapture() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(InvalidBothUsesViolateCapture.class)
        );
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    public static class InvalidFirstUseViolatesCapture implements Foo {
        public void wat(Float in1, Integer in2) { }
    }
    @Test
    void test_invalid_firstUseViolatesCapture() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(InvalidFirstUseViolatesCapture.class)
        );
    }


    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    public static class InvalidSecondUseViolatesCapture implements Foo {
        public void wat(Integer in1, Float in2) { }
    }
    @Test
    void test_invalid_secondUseViolatesCapture() {
        Assertions.assertThrows(
                AbstractionException.ClashingArgumentTypeException.class,
                () -> Abstraction.check(InvalidSecondUseViolatesCapture.class)
        );
    }
}