package io.github.skibrianski.partial_interface;

import io.github.skibrianski.partial_interface.internal.IType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TypeParameterParserTest {

    @Test
    void test_unparameterizedScalarType() {
        TypeParameterResolver typeParameterResolver = new TypeParameterResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeParameterResolver);

        IType intType = typeParameterParser.parse("int");
        Assertions.assertEquals(int.class, intType.getActualType());
    }

    @Test
    void test_unparameterizedArrayType() {
        TypeParameterResolver typeParameterResolver = new TypeParameterResolver(Map.of());
        TypeParameterParser typeParameterParser = new TypeParameterParser(typeParameterResolver);

        IType intArrayType = typeParameterParser.parse("int[]");
        Assertions.assertEquals(int[].class, intArrayType.getActualType());
    }

//    @Test
//    void test_Integer() {
//        TypeParameterResolver typeParameterResolver = new TypeParameterResolver(Map.of());
//        TypeParameterParser typeParameterParser = new TypeParameterParser(typeParameterResolver);
//
//        IType integerType = typeParameterParser.parse("java.lang.Integer");
//        Assertions.assertEquals(Integer.class, integerType.getActualType());
//    }
}