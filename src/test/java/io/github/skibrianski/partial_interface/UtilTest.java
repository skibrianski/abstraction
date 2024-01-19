package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
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
                new Class<?>[0],
                false
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
                new Class<?>[]{String.class},
                false
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
                new Class<?>[]{String.class, Object.class},
                false
        );
        Assertions.assertEquals(
                "int foo(String, Object)",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_toString_withVarArgs() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                false,
                int.class,
                "foo",
                new Class<?>[]{String.class, byte.class},
                true
        );
        Assertions.assertEquals(
                "int foo(String, byte...)",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_toString_static() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                true,
                int.class,
                "foo",
                new Class<?>[0],
                false
        );
        Assertions.assertEquals(
                "static int foo()",
                PartialInterface.Util.stringify(partialInterface)
        );
    }

    @Test
    void test_validate_valid() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                true,
                int.class,
                "foo",
                new Class<?>[0],
                false
        );
        Assertions.assertDoesNotThrow(() -> PartialInterface.Util.validate(partialInterface));
    }

    @Test
    void test_validate_invalidVarArgs() {
        PartialInterface partialInterface = TestUtil.buildPartialInterface(
                true,
                int.class,
                "foo",
                new Class<?>[0],
                true
        );
        Assertions.assertThrows(
                PartialInterfaceUsageException.class,
                () -> PartialInterface.Util.validate(partialInterface)
        );
    }
}