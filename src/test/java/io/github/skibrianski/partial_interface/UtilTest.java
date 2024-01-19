package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;


public class UtilTest {

    @Test
    void test_toString_withNoArguments() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                false,
                int.class,
                "foo",
                new Class<?>[0]
        );
        Assertions.assertEquals(
                "int foo()",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_toString_withSingleArgument() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                false,
                int.class,
                "foo",
                new Class<?>[]{String.class}
        );
        Assertions.assertEquals(
                "int foo(String)",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_toString_withMultipleArguments() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                false,
                int.class,
                "foo",
                new Class<?>[]{String.class, Object.class}
        );
        Assertions.assertEquals(
                "int foo(String, Object)",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_toString_static() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                true,
                int.class,
                "foo",
                new Class<?>[0]
        );
        Assertions.assertEquals(
                "static int foo()",
                PartialInterface.Util.stringify(partialInterface)
        );
    }
}