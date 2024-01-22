package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeParameterResolverTest {

    @Test
    void test_isEmpty() {
        Assertions.assertTrue(new TypeParameterResolver(Map.of()).isEmpty());
        Assertions.assertFalse(new TypeParameterResolver(Map.of("I", int.class)).isEmpty());
    }

    @Test
    void test_scalar() {
        String typeName = "R";
        String somethingElse = typeName + "x";
        TypeParameterResolver typeParameterResolver = new TypeParameterResolver(
                Map.of(typeName, int.class)
        );

        Assertions.assertFalse(typeParameterResolver.canResolve(somethingElse));
        Assertions.assertTrue(typeParameterResolver.canResolve(typeName));
        Assertions.assertNull(typeParameterResolver.resolve(somethingElse));
        Assertions.assertEquals(int.class, typeParameterResolver.resolve(typeName));
        Assertions.assertThrows(
                PartialInterfaceNotCompletedException.class,
                () -> typeParameterResolver.mustResolve(somethingElse)
        );
        Assertions.assertEquals(int.class, typeParameterResolver.mustResolve(typeName));
    }

    @Test
    void test_array() {
        String scalarTypeName = "R";
        TypeParameterResolver typeParameterResolver = new TypeParameterResolver(
                Map.of(scalarTypeName, int.class)
        );

        Assertions.assertEquals(int[].class, typeParameterResolver.resolve("R[]"));
        Assertions.assertEquals(int[].class, typeParameterResolver.resolve("R..."));
        Assertions.assertEquals(int[][].class, typeParameterResolver.resolve("R[][]"));
        Assertions.assertEquals(int[][].class, typeParameterResolver.resolve("R[]..."));
    }
}