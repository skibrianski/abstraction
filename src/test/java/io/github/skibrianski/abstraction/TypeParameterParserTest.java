package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeParameterParserTest {
    @Test
    void test_unknownType() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("NotAValidType", typeNameResolver);

        Assertions.assertThrows(
                AbstractionException.UsageException.class,
                typeParameterParser::parse
        );
    }
    
    @Test
    void test_unparameterized_primitive_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("int", typeNameResolver);

        Assertions.assertEquals(int.class, typeParameterParser.parse());
    }

    @Test
    void test_unparameterized_primitive_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("int[]", typeNameResolver);

        Assertions.assertEquals(int[].class, typeParameterParser.parse());
    }

    @Test
    void test_unparameterized_multiDimensionalArray() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("double[][][]", typeNameResolver);

        Assertions.assertEquals(double[][][].class, typeParameterParser.parse());
    }

    @Test
    void test_unparameterized_builtIn_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("Integer", typeNameResolver);

        Assertions.assertEquals(Integer.class, typeParameterParser.parse());
    }

    @Test
    void test_unparameterized_builtIn_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("Comparable[]", typeNameResolver);

        Assertions.assertEquals(Comparable[].class, typeParameterParser.parse());
    }

    @Test
    void test_unparameterized_withNonVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("List<Integer>", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
        Assertions.assertEquals(1, actualTypeArguments.length);

        Type parameterInternalType = actualTypeArguments[0];
        Assertions.assertInstanceOf(Class.class, parameterInternalType);
        Assertions.assertEquals(Integer.class, parameterInternalType);
    }

    @Test
    void test_parameterized_scalar() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("R", int.class);
        TypeParameterParser typeParameterParser = new TypeParameterParser("R", typeNameResolver);

        Assertions.assertEquals(int.class, typeParameterParser.parse());
    }

    @Test
    void test_parameterized_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("R", int.class);

        Assertions.assertEquals(int[].class, new TypeParameterParser("R...", typeNameResolver).parse());
        Assertions.assertEquals(int[].class, new TypeParameterParser("R[]", typeNameResolver).parse());
    }
    @Test
    void test_parameterizedWithParameters_array() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("Map<String, UUID>[]...", typeNameResolver);

        Type doubleArrayType = typeParameterParser.parse();
        Assertions.assertInstanceOf(GenericArrayType.class, doubleArrayType);
        Type singleArrayType = ((GenericArrayType) doubleArrayType).getGenericComponentType();
        Assertions.assertInstanceOf(GenericArrayType.class, singleArrayType);
        Type scalarType = ((GenericArrayType) singleArrayType).getGenericComponentType();
        Assertions.assertInstanceOf(ParameterizedType.class, scalarType);
        ParameterizedType parameterizedScalarType = (ParameterizedType) scalarType;
        Type listType = parameterizedScalarType.getRawType();
        Assertions.assertEquals(Map.class, listType);
        Type[] typeArguments = parameterizedScalarType.getActualTypeArguments();
        Assertions.assertEquals(2, typeArguments.length);
        Assertions.assertEquals(String.class, typeArguments[0]);
        Assertions.assertEquals(UUID.class, typeArguments[1]);
    }
    @Test
    void test_parameterized_withVariableTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("R", String.class);
        TypeParameterParser typeParameterParser = new TypeParameterParser("List<R>", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
        Assertions.assertEquals(1, actualTypeArguments.length);

        Type parameterInternalType = actualTypeArguments[0];
        Assertions.assertInstanceOf(Class.class, parameterInternalType);
        Assertions.assertEquals(String.class, parameterInternalType);
    }
    @Test
    void test_parameterized_withMultipleTypeParameters() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("R", String.class);
        TypeParameterParser typeParameterParser = new TypeParameterParser("Map<UUID, R>", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(Map.class, parameterizedBaseType.getRawType());
        Type[] actualTypeArguments = parameterizedBaseType.getActualTypeArguments();
        Assertions.assertEquals(2, actualTypeArguments.length);

        Type firstParameterInternalType = actualTypeArguments[0];
        Assertions.assertInstanceOf(Class.class, firstParameterInternalType);
        Assertions.assertEquals(UUID.class, firstParameterInternalType);

        Type secondParameterInternalType = actualTypeArguments[1];
        Assertions.assertInstanceOf(Class.class, secondParameterInternalType);
        Assertions.assertEquals(String.class, secondParameterInternalType);
    }
    @Test
    void test_parameterized_withNestedTypeParameters() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("R", char.class);
        TypeParameterParser typeParameterParser = new TypeParameterParser("Map<List<String>, R>", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(Map.class, parameterizedBaseType.getRawType());
        Type[] topLevelTypeArguments = parameterizedBaseType.getActualTypeArguments();
        Assertions.assertEquals(2, topLevelTypeArguments.length);

        Type firstParameterInternalType = topLevelTypeArguments[0];
        Assertions.assertInstanceOf(ParameterizedType.class, firstParameterInternalType);
        ParameterizedType firstParameterParameterType = (ParameterizedType) firstParameterInternalType;
        Assertions.assertEquals(List.class, firstParameterParameterType.getRawType());
        Type[] firstParameterTypeArgs = firstParameterParameterType.getActualTypeArguments();

        Type firstSubParameterInternalType = firstParameterTypeArgs[0];
        Assertions.assertInstanceOf(Class.class, firstSubParameterInternalType);
        Assertions.assertEquals(String.class, firstSubParameterInternalType);

        Type secondParameterInternalType = topLevelTypeArguments[1];
        Assertions.assertInstanceOf(Class.class, secondParameterInternalType);
        Assertions.assertEquals(char.class, secondParameterInternalType);
    }

    @Test
    void test_parameterized_withTypeVariableAsBaseOfTypeParameter() {
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("C", List.class);
        TypeParameterParser typeParameterParser = new TypeParameterParser("C<String>", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedBaseType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(List.class, parameterizedBaseType.getRawType());
        Type[] firstParameterTypeArgs = parameterizedBaseType.getActualTypeArguments();
        Assertions.assertEquals(1, firstParameterTypeArgs.length);

        Type firstParameterInternalType = firstParameterTypeArgs[0];
        Assertions.assertInstanceOf(Class.class, firstParameterInternalType);
        Assertions.assertEquals(String.class, firstParameterInternalType);
    }

    @Test
    void test_singleWildcardExtends() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("? extends Number", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(WildcardType.class, baseInternalType);
        WildcardType wildcardType = (WildcardType) baseInternalType;
        Assertions.assertArrayEquals(new Type[]{Number.class}, wildcardType.getUpperBounds());
        Assertions.assertArrayEquals(new Type[]{}, wildcardType.getLowerBounds());
    }

    @Test
    void test_singleWildcardSuper() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("? super Number", typeNameResolver);

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(WildcardType.class, baseInternalType);
        WildcardType wildcardType = (WildcardType) baseInternalType;
        Assertions.assertArrayEquals(new Type[]{}, wildcardType.getUpperBounds());
        Assertions.assertArrayEquals(new Type[]{Number.class}, wildcardType.getLowerBounds());
    }

    @Test
    void test_neitherSuperNorExtendsKeyword() {
        TypeNameResolver typeNameResolver = new TypeNameResolver();
        TypeParameterParser typeParameterParser = new TypeParameterParser("? daya Number", typeNameResolver);

        Assertions.assertThrows(
                AbstractionException.UsageException.class,
                () -> typeParameterParser.parse()
        );
    }

    @Test
    void test_complex() {
        // @RequiresTypeParameter(value = "T", extending = "Number")
        // @RequiresTypeParameter(value = "U", extending = "Collection<? extends T>")
        // @RequiresTypeParameter(value = "V", extending = "Map<? extends T, ? extends U>")
        // TODO: parser is reading Map<? extends T, ? extends U> wrong.
        // it parses this as a single type argument to Map which extends both `T` and `? extends U` which is wrong.
        // how can we solve this ambiguity?
        WildcardType extendingNumberWildcardType = new TypeParameterParser.WildcardTypeImpl(
                new Type[]{},
                new Type[]{Number.class}
        );
        ParameterizedType collectionOfExtendingNumberType = new TypeParameterParser.ParameterizedTypeImpl(
                Collection.class,
                new Type[]{extendingNumberWildcardType}
        );
        TypeNameResolver typeNameResolver = new TypeNameResolver()
                .addTypeParameter("T", Number.class)
                .addTypeParameter("U", collectionOfExtendingNumberType);
        TypeParameterParser typeParameterParser = new TypeParameterParser(
                "Map<? extends T, ? extends U>",
                typeNameResolver
        );

        Type baseInternalType = typeParameterParser.parse();
        Assertions.assertInstanceOf(ParameterizedType.class, baseInternalType);
        ParameterizedType parameterizedType = (ParameterizedType) baseInternalType;
        Assertions.assertEquals(Map.class, parameterizedType.getRawType());
        Assertions.assertEquals(2, parameterizedType.getActualTypeArguments().length);

        var z = 123;
//        WildcardType wildcardType = (WildcardType) baseInternalType;
//        Assertions.assertArrayEquals(new Type[]{Number.class}, wildcardType.getUpperBounds());
//        Assertions.assertArrayEquals(new Type[]{}, wildcardType.getLowerBounds());
    }

}