//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import testutil.TestUtil;
//
//
//public class UtilTest {
//
//    @Test
//    void test_toString_withVoidReturnAndNoArguments() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                false,
//                TestUtil.buildRegularType(void.class),
//                "foo",
//                new Type[]{}
//        );
//        Assertions.assertEquals(
//                "void foo()",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//
//    @Test
//    void test_toString_withNoArguments() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                false,
//                TestUtil.buildRegularType(int.class),
//                "foo",
//                new Type[]{}
//        );
//        Assertions.assertEquals(
//                "int foo()",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//
//    @Test
//    void test_toString_withSingleArgument() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                false,
//                TestUtil.buildRegularType(int.class),
//                "foo",
//                new Type[]{TestUtil.buildRegularType(String.class)}
//        );
//        Assertions.assertEquals(
//                "int foo(String)",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//
//    @Test
//    void test_toString_withMultipleArguments() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                false,
//                TestUtil.buildRegularType(int.class),
//                "foo",
//                new Type[]{
//                        TestUtil.buildRegularType(String.class),
//                        TestUtil.buildRegularType(Object.class)
//                }
//        );
//        Assertions.assertEquals(
//                "int foo(String, Object)",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//
//    @Test
//    void test_toString_static() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                true,
//                TestUtil.buildRegularType(int.class),
//                "foo",
//                new Type[]{}
//        );
//        Assertions.assertEquals(
//                "static int foo()",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//
//    @Test
//    void test_toString_parameterizedType() {
//        RequiresChildMethod requiresChildMethod = TestUtil.buildRequiresChildMethod(
//                false,
//                TestUtil.buildParameterizedType("R"),
//                "foo",
//                new Type[]{}
//        );
//        Assertions.assertEquals(
//                "R foo()",
//                RequiresChildMethod.Util.stringify(requiresChildMethod)
//        );
//    }
//}