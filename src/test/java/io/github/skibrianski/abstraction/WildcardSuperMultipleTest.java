package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardSuperMultipleTest {

    static class Vehicle {}
    static class LandVehicle extends Vehicle {}
    static class WaterVehicle extends Vehicle {}
    class Car extends LandVehicle {}
    class Boat extends WaterVehicle {}

    @RequiresTypeParameter(value = "T", superOf = {"Car", "Boat"}) // Vehicle
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("T")},
            methodName = "method"
    )
    interface WithMethod { }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Vehicle.class)
    static class ValidSubClass implements WithMethod {
        public void method(Vehicle foo) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubClass.class));
    }

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = LandVehicle.class)
    static class Invalid implements WithMethod {
        public void method(LandVehicle foo) { }
    }
    @Test
    void test_valid_exactMatch() {
        Assertions.assertThrows(
                AbstractionException.TypeParameterViolatesBoundsException.class,
                () -> Abstraction.check(Invalid.class)
        );
    }

}