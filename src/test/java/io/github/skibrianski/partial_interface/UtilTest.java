package io.github.skibrianski.partial_interface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;


public class UtilTest {

    @Test
    void test_toString_withNoArguments() {
        RequiresChildMethod requiresChildMethod = TestUtil.buildAnnotation(
                false,
                TestUtil.buildRegularType(int.class),
                "foo",
                new Class<?>[0]
        );
        Assertions.assertEquals(
                "int foo()",
                RequiresChildMethod.Util.stringify(requiresChildMethod)
        );
    }

    @Test
    void test_toString_withSingleArgument() {
        RequiresChildMethod requiresChildMethod = TestUtil.buildAnnotation(
                false,
                TestUtil.buildRegularType(int.class),
                "foo",
                new Class<?>[]{String.class}
        );
        Assertions.assertEquals(
                "int foo(String)",
                RequiresChildMethod.Util.stringify(requiresChildMethod)
        );
    }

    @Test
    void test_toString_withMultipleArguments() {
        RequiresChildMethod requiresChildMethod = TestUtil.buildAnnotation(
                false,
                TestUtil.buildRegularType(int.class),
                "foo",
                new Class<?>[]{String.class, Object.class}
        );
        Assertions.assertEquals(
                "int foo(String, Object)",
                RequiresChildMethod.Util.stringify(requiresChildMethod)
        );
    }

    @Test
    void test_toString_static() {
        RequiresChildMethod requiresChildMethod = TestUtil.buildAnnotation(
                true,
                TestUtil.buildRegularType(int.class),
                "foo",
                new Class<?>[0]
        );
        Assertions.assertEquals(
                "static int foo()",
                RequiresChildMethod.Util.stringify(requiresChildMethod)
        );
    }
}