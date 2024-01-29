package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeNameResolverTest {

    @Test
    void test_scalar() {
        String typeName = "R";
        String somethingElse = typeName + "x";
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter(typeName, int.class);

        Assertions.assertFalse(typeNameResolver.canResolve(somethingElse));
        Assertions.assertTrue(typeNameResolver.canResolve(typeName));
        Assertions.assertNull(typeNameResolver.resolve(somethingElse));
        Assertions.assertEquals(int.class, typeNameResolver.resolve(typeName));
        Assertions.assertThrows(
                AbstractionException.NotCompletedException.class,
                () -> typeNameResolver.mustResolve(somethingElse)
        );
        Assertions.assertEquals(int.class, typeNameResolver.mustResolve(typeName));
    }

    @Test
    void test_array() {
        String scalarTypeName = "R";
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter(scalarTypeName, int.class);

        Assertions.assertEquals(int[].class, typeNameResolver.resolve("R[]"));
        Assertions.assertEquals(int[].class, typeNameResolver.resolve("R..."));
        Assertions.assertEquals(int[][].class, typeNameResolver.resolve("R[][]"));
        Assertions.assertEquals(int[][].class, typeNameResolver.resolve("R[]..."));
    }
}