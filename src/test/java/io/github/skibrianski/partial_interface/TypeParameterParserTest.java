//package io.github.skibrianski.partial_interface;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class TypeParameterParserTest {
//
//    @Test
//    void test_unknownType() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertThrows(
//                PartialInterfaceException.UsageException.class,
//                () -> typeParameterParser.parse("NotAValidType")
//        );
//    }
//
//    @Test
//    void test_unparameterized_primitive_scalar() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(int.class, typeParameterParser.parse("int"));
//    }
//
//    @Test
//    void test_unparameterized_primitive_array() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(int[].class, typeParameterParser.parse("int[]"));
//    }
//
//    @Test
//    void test_unparameterized_multiDimensionalArray() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(double[][][].class, typeParameterParser.parse("double[][][]"));
//    }
//
//    @Test
//    void test_unparameterized_builtIn_scalar() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(Integer.class, typeParameterParser.parse("Integer"));
//    }
//
//    @Test
//    void test_unparameterized_builtIn_array() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(Comparable[].class, typeParameterParser.parse("Comparable[]"));
//    }
//
//    @Test
//    void test_unparameterized_withNonVariableTypeParameter() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Type baseInternalType = typeParameterParser.parse("List<Integer>");
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
//        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
//        Assertions.assertEquals(1, actualTypeArguments.length);
//
//        Type parameterInternalType = actualTypeArguments[0];
//        Assertions.assertInstanceOf(Class.class, parameterInternalType);
//        Assertions.assertEquals(Integer.class, parameterInternalType);
//    }
//
//    @Test
//    void test_parameterized_scalar() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(int.class, typeParameterParser.parse("R"));
//    }
//
//    @Test
//    void test_parameterized_array() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", int.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Assertions.assertEquals(int[].class, typeParameterParser.parse("R..."));
//        Assertions.assertEquals(int[].class, typeParameterParser.parse("R[]"));
//    }
//
//    @Test
//    void test_parameterized_withVariableTypeParameter() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Type baseInternalType = typeParameterParser.parse("List<R>");
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
//        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
//        Assertions.assertEquals(1, actualTypeArguments.length);
//
//        Type parameterInternalType = actualTypeArguments[0];
//        Assertions.assertInstanceOf(Class.class, parameterInternalType);
//        Assertions.assertEquals(String.class, parameterInternalType);
//    }
//
//    @Test
//    void test_parameterized_withMultipleTypeParameters() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", String.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Type baseInternalType = typeParameterParser.parse("Map<UUID, R>");
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(Map.class, parameterizedBaseType.getRawType());
//        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
//        Assertions.assertEquals(2, actualTypeArguments.length);
//
//        Type firstParameterInternalType = actualTypeArguments[0];
//        Assertions.assertInstanceOf(Class.class, firstParameterInternalType);
//        Assertions.assertEquals(UUID.class, firstParameterInternalType);
//
//        Type secondParameterInternalType = actualTypeArguments[1];
//        Assertions.assertInstanceOf(Class.class, secondParameterInternalType);
//        Assertions.assertEquals(String.class, secondParameterInternalType);
//    }
//
//    @Test
//    void test_parameterized_withNestedTypeParameters() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("R", char.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Type baseInternalType = typeParameterParser.parse("Map<List<String>, R>");
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(Map.class, parameterizedBaseType.getRawType());
//        Type[] topLevelTypeArguments = parameterizedBaseType.getActualTypeArguments();
//        Assertions.assertEquals(2, topLevelTypeArguments.length);
//
//        Type firstParameterInternalType = topLevelTypeArguments[0];
//        Assertions.assertInstanceOf(ParameterizedType.class, firstParameterInternalType);
//        ParameterizedType firstParameterParameterType = (ParameterizedType) firstParameterInternalType;
//        Assertions.assertEquals(List.class, firstParameterParameterType.getRawType());
//        Type[] firstParameterTypeArgs = firstParameterParameterType.getActualTypeArguments();
//
//        Type firstSubParameterInternalType = firstParameterTypeArgs[0];
//        Assertions.assertInstanceOf(Class.class, firstSubParameterInternalType);
//        Assertions.assertEquals(String.class, firstSubParameterInternalType);
//
//        Type secondParameterInternalType = topLevelTypeArguments[1];
//        Assertions.assertInstanceOf(Class.class, secondParameterInternalType);
//        Assertions.assertEquals(char.class, secondParameterInternalType);
//    }
//
//    @Test
//    void test_parameterized_withTypeVariableAsBaseOfTypeParameter() {
//        TypeNameResolver typeNameResolver = new TypeNameResolver(Map.of("C", List.class));
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
//
//        Type baseInternalType = typeParameterParser.parse("C<String>");
//        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
//        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
//        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
//        Type[] firstParameterTypeArgs = parameterizedBaseType.getActualTypeArguments();
//        Assertions.assertEquals(1, firstParameterTypeArgs.length);
//
//        Type firstParameterInternalType = firstParameterTypeArgs[0];
//        Assertions.assertInstanceOf(Class.class, firstParameterInternalType);
//        Assertions.assertEquals(String.class, firstParameterInternalType);
//    }
//}