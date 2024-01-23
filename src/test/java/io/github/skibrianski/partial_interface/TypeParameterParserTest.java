package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.ClassType;
import io.github.skibrianski.partial_interface.internal.IType;
import io.github.skibrianski.partial_interface.internal.ParameterizedType;
import io.github.skibrianski.partial_interface.internal.TypeVariable;
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

        IType baseInternalType = typeParameterParser.parse("List<Integer>");
        Assertions.assertEquals(List.class, baseInternalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(1, parameterizedBaseType.getParameters().size());

        IType parameterInternalType = parameterizedBaseType.getParameters().get(0);
        Assertions.assertInstanceOf(ClassType.class, parameterInternalType);
        Assertions.assertEquals(Integer.class, parameterInternalType.getActualType());
    }


    @Test
    void test_unparameterizedObject_withVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType baseInternalType = typeParameterParser.parse("List<R>");
        Assertions.assertEquals(List.class, baseInternalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(1, parameterizedBaseType.getParameters().size());

        IType parameterInternalType = parameterizedBaseType.getParameters().get(0);
        Assertions.assertInstanceOf(TypeVariable.class, parameterInternalType);
        Assertions.assertEquals(String.class, parameterInternalType.getActualType());
    }

    @Test
    void test_unparameterizedObject_withMultipleTypeParameters() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType baseInternalType = typeParameterParser.parse("Map<UUID, R>");
        Assertions.assertEquals(Map.class, baseInternalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(2, parameterizedBaseType.getParameters().size());

        IType firstParameterInternalType = parameterizedBaseType.getParameters().get(0);
        Assertions.assertInstanceOf(ClassType.class, firstParameterInternalType);
        Assertions.assertEquals(UUID.class, firstParameterInternalType.getActualType());

        IType secondParameterInternalType = parameterizedBaseType.getParameters().get(1);
        Assertions.assertInstanceOf(TypeVariable.class, secondParameterInternalType);
        Assertions.assertEquals(String.class, secondParameterInternalType.getActualType());
    }

    @Test
    void test_unparameterizedObject_withNestedTypeParameters() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", char.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        IType baseInternalType = typeParameterParser.parse("Map<List<String>, R>");
        Assertions.assertEquals(Map.class, baseInternalType.getActualType());
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType baseParameterizedType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(2, baseParameterizedType.getParameters().size());

        IType firstParameterInternalType = baseParameterizedType.getParameters().get(0);
        Assertions.assertInstanceOf(ParameterizedType.class, firstParameterInternalType);
        Assertions.assertEquals(List.class, firstParameterInternalType.getActualType());

        IType firstSubParameterInternalType = ((ParameterizedType) firstParameterInternalType).getParameters().get(0);
        Assertions.assertInstanceOf(ClassType.class, firstSubParameterInternalType);
        Assertions.assertEquals(String.class, firstSubParameterInternalType.getActualType());

        IType secondParameterInternalType = baseParameterizedType.getParameters().get(1);
        Assertions.assertInstanceOf(TypeVariable.class, secondParameterInternalType);
        Assertions.assertEquals(char.class, secondParameterInternalType.getActualType());
    }
}