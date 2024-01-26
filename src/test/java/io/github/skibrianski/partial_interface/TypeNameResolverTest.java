//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.Map;
//
//public class TypeNameResolverTest {
//
//    @Test
//    void test_isEmpty() {
//        Assertions.assertTrue(new TypeNameResolver(Map.of()).isEmpty());
//        Assertions.assertFalse(new TypeNameResolver(Map.of("I", int.class)).isEmpty());
//    }
//
//    @Test
//    void test_scalar() {
//        String typeName = "R";
//        String somethingElse = typeName + "x";
//        TypeNameResolver typeNameResolver = new TypeNameResolver(
//                Map.of(typeName, int.class)
//        );
//
//        Assertions.assertFalse(typeNameResolver.canResolve(somethingElse));
//        Assertions.assertTrue(typeNameResolver.canResolve(typeName));
//        Assertions.assertNull(typeNameResolver.resolve(somethingElse));
//        Assertions.assertEquals(int.class, typeNameResolver.resolve(typeName));
//        Assertions.assertThrows(
//                PartialInterfaceException.NotCompletedException.class,
//                () -> typeNameResolver.mustResolve(somethingElse)
//        );
//        Assertions.assertEquals(int.class, typeNameResolver.mustResolve(typeName));
//    }
//
//    @Test
//    void test_array() {
//        String scalarTypeName = "R";
//        TypeNameResolver typeNameResolver = new TypeNameResolver(
//                Map.of(scalarTypeName, int.class)
//        );
//
//        Assertions.assertEquals(int[].class, typeNameResolver.resolve("R[]"));
//        Assertions.assertEquals(int[].class, typeNameResolver.resolve("R..."));
//        Assertions.assertEquals(int[][].class, typeNameResolver.resolve("R[][]"));
//        Assertions.assertEquals(int[][].class, typeNameResolver.resolve("R[]..."));
//    }
//}