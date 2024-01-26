package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExtensionRulesTest {


    interface Foo {
        int foo(int bar);
    }

    interface Foo2 extends Foo {
//        void foo(int bar); // not ok
//        int foo(int bar); // ok
    }

    class Foo3 implements Foo {

        public int foo(int bar) { return 3; } // ok
//        public void foo(int bar) { } // not ok
    }

    @RequiresChildMethod(
            returnType = @Type(ofClass = int.class),
            argumentTypes = {@Type(ofClass = int.class)},
            methodName = "foo"
    )
    interface WithFoo { }

    @PartialInterfaceWithManualValidation
    interface ValidInterfaceFull extends WithFoo {
        int foo(int bar);
    }
    @Test
    void test_valid() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterfaceFull.class));
    }

    @PartialInterfaceWithManualValidation
    interface ValidInterfaceEmpty extends WithFoo {
    }
    @Test
    void test_invalid_returnTypeClash() {
        Assertions.assertDoesNotThrow(() -> PartialInterface.check(ValidInterfaceEmpty.class));
    }

}