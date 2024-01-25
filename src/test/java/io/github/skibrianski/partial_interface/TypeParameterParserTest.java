package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeParameterParserTest {

    @Test
    void test_unknownType() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertThrows(
                PartialInterfaceUsageException.class,
                () -> typeParameterParser.parse("NotAValidType")
        );
    }

    @Test
    void test_unparameterized_primitive_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(int.class, typeParameterParser.parse("int"));
    }

    @Test
    void test_unparameterized_primitive_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(int[].class, typeParameterParser.parse("int[]"));
    }

    @Test
    void test_unparameterized_multiDimensionalArray() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(double[][][].class, typeParameterParser.parse("double[][][]"));
    }

    @Test
    void test_unparameterized_builtIn_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(Integer.class, typeParameterParser.parse("Integer"));
    }

    @Test
    void test_unparameterized_builtIn_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(Comparable[].class, typeParameterParser.parse("Comparable[]"));
    }

    @Test
    void test_unparameterized_withNonVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Type baseInternalType = typeParameterParser.parse("List<Integer>");
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
        Assertions.assertEquals(1, parameterizedBaseType.getActualTypeArguments().length);

        Type parameterInternalType = parameterizedBaseType.getActualTypeArguments()[0];
        Assertions.assertInstanceOf(Class.class, parameterInternalType);
        Assertions.assertEquals(Integer.class, parameterInternalType);
    }

    @Test
    void test_parameterized_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(int.class, typeParameterParser.parse("R"));
    }

    @Test
    void test_parameterized_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);

        Assertions.assertEquals(int[].class, typeParameterParser.parse("R..."));
        Assertions.assertEquals(int[].class, typeParameterParser.parse("R[]"));
    }

//    @Test
//    void test_parameterized_withVariableTypeParameter() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        IType baseInternalType = typeParameterParser.parse("List<R>");
//        Assertions.assertEquals(List.class, baseInternalType.getActualType());
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(1, parameterizedBaseType.getParameters().size());
//
//        IType parameterInternalType = parameterizedBaseType.getParameters().get(0);
//        Assertions.assertInstanceOf(ClassType.class, parameterInternalType);
//        Assertions.assertEquals(String.class, parameterInternalType.getActualType());
//    }
//
//    @Test
//    void test_parameterized_withMultipleTypeParameters() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        IType baseInternalType = typeParameterParser.parse("Map<UUID, R>");
//        Assertions.assertEquals(Map.class, baseInternalType.getActualType());
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(2, parameterizedBaseType.getParameters().size());
//
//        IType firstParameterInternalType = parameterizedBaseType.getParameters().get(0);
//        Assertions.assertInstanceOf(ClassType.class, firstParameterInternalType);
//        Assertions.assertEquals(UUID.class, firstParameterInternalType.getActualType());
//
//        IType secondParameterInternalType = parameterizedBaseType.getParameters().get(1);
//        Assertions.assertInstanceOf(ClassType.class, secondParameterInternalType);
//        Assertions.assertEquals(String.class, secondParameterInternalType.getActualType());
//    }
//
//    @Test
//    void test_parameterized_withNestedTypeParameters() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", char.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        IType baseInternalType = typeParameterParser.parse("Map<List<String>, R>");
//        Assertions.assertEquals(Map.class, baseInternalType.getActualType());
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType baseParameterizedType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(2, baseParameterizedType.getParameters().size());
//
//        IType firstParameterInternalType = baseParameterizedType.getParameters().get(0);
//        Assertions.assertInstanceOf(ParameterizedType.class, firstParameterInternalType);
//        Assertions.assertEquals(List.class, firstParameterInternalType.getActualType());
//
//        IType firstSubParameterInternalType = ((ParameterizedType) firstParameterInternalType).getParameters().get(0);
//        Assertions.assertInstanceOf(ClassType.class, firstSubParameterInternalType);
//        Assertions.assertEquals(String.class, firstSubParameterInternalType.getActualType());
//
//        IType secondParameterInternalType = baseParameterizedType.getParameters().get(1);
//        Assertions.assertInstanceOf(ClassType.class, secondParameterInternalType);
//        Assertions.assertEquals(char.class, secondParameterInternalType.getActualType());
//    }
//
//    @Test
//    void test_parameterized_withTypeVariableAsBaseOfTypeParameter() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("C", List.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        IType baseInternalType = typeParameterParser.parse("C<String>");
//        Assertions.assertEquals(List.class, baseInternalType.getActualType());
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType baseParameterizedType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(1, baseParameterizedType.getParameters().size());
//
//        IType firstParameterInternalType = baseParameterizedType.getParameters().get(0);
//        Assertions.assertInstanceOf(ClassType.class, firstParameterInternalType);
//        Assertions.assertEquals(String.class, firstParameterInternalType.getActualType());
//    }
}