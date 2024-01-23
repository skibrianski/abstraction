package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.ParameterizedType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeParameterParserTest {

    @Test
    void test_unparameterizedPrimitive_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType intType = typeParameterParser.parse("int");
        Assertions.assertEquals(int.class, intType.getActualType());
    }

    @Test
    void test_unparameterizedPrimitive_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType intArrayType = typeParameterParser.parse("int[]");
        Assertions.assertEquals(int[].class, intArrayType.getActualType());
    }

    @Test
    void test_multiDimensionalArray() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType intArrayType = typeParameterParser.parse("double[][][]");
        Assertions.assertEquals(double[][][].class, intArrayType.getActualType());
    }

    @Test
    void test_unparameterizedObjectBuiltIn_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType integerType = typeParameterParser.parse("Integer");
        Assertions.assertEquals(Integer.class, integerType.getActualType());
    }

    @Test
    void test_unparameterizedObjectBuiltIn_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType integerType = typeParameterParser.parse("Comparable[]");
        Assertions.assertEquals(Comparable[].class, integerType.getActualType());
    }

    @Test
    void test_parameterized_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType integerType = typeParameterParser.parse("R");
        Assertions.assertEquals(int.class, integerType.getActualType());
    }

    @Test
    void test_parameterized_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(int[].class, typeParameterParser.parse("R...").getActualType());
        Assertions.assertEquals(int[].class, typeParameterParser.parse("R[]").getActualType());
    }


    @Test
    void test_unparameterizedObject_withNonVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType internalType = typeParameterParser.parse("List<Integer>");
        Assertions.assertEquals(List.class, internalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, internalType);
        ParameterizedType parameterizedType = (ParameterizedType) internalType;
        Assertions.assertEquals(1, parameterizedType.getParameters().size());
        Assertions.assertEquals(Integer.class, parameterizedType.getParameters().get(0).getActualType());
    }


    @Test
    void test_unparameterizedObject_withVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType internalType = typeParameterParser.parse("List<R>");
        Assertions.assertEquals(List.class, internalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, internalType);
        ParameterizedType parameterizedType = (ParameterizedType) internalType;
        Assertions.assertEquals(1, parameterizedType.getParameters().size());
        Assertions.assertEquals(String.class, parameterizedType.getParameters().get(0).getActualType());
    }

    @Test
    void test_unparameterizedObject_withMultipleTypeParameters() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType internalType = typeParameterParser.parse("Map<UUID, R>");
        Assertions.assertEquals(Map.class, internalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, internalType);
        ParameterizedType parameterizedType = (ParameterizedType) internalType;
        Assertions.assertEquals(2, parameterizedType.getParameters().size());
        Assertions.assertEquals(UUID.class, parameterizedType.getParameters().get(0).getActualType());
        Assertions.assertEquals(String.class, parameterizedType.getParameters().get(1).getActualType());
    }
}