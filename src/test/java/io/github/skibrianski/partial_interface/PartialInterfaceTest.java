package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Test;

public class PartialInterfaceTest {

    @PartialInterface(returnType = String.class, argumentTypes = {String.class}, methodName = "concat")
    interface WithConcatenation { }

    @Test
    void test_foo() {

    }
}