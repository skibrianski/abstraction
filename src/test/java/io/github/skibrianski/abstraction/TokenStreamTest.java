package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TokenStreamTest {

    @Test
    void test_lifecycle() {
        List<TypeParameterToken> tokens = List.of(
                new TypeParameterToken.Variable("bob"),
                TypeParameterToken.StaticToken.OPEN_ARRAY,
                TypeParameterToken.StaticToken.CLOSE_ARRAY
        );
        TokenStream tokenStream = new TokenStream(tokens);

        Assertions.assertFalse(tokenStream.isDone());
        Assertions.assertTrue(tokenStream.hasNMoreTokens(3));
        Assertions.assertFalse(tokenStream.hasNMoreTokens(4));
        Assertions.assertTrue(tokenStream.nextTokenIsVariable());

        Assertions.assertEquals(tokens.get(0), tokenStream.consume());
        Assertions.assertTrue(tokenStream.hasNMoreTokens(2));
        Assertions.assertFalse(tokenStream.hasNMoreTokens(3));
        Assertions.assertTrue(tokenStream.nextTokensAre(
                TypeParameterToken.StaticToken.OPEN_ARRAY,
                TypeParameterToken.StaticToken.CLOSE_ARRAY
        ));

        Assertions.assertEquals(tokens.get(1), tokenStream.consume());
        Assertions.assertTrue(tokenStream.hasNMoreTokens(1));
        Assertions.assertFalse(tokenStream.hasNMoreTokens(2));
        Assertions.assertTrue(tokenStream.nextTokenIs(TypeParameterToken.StaticToken.CLOSE_ARRAY));

        Assertions.assertEquals(tokens.get(2), tokenStream.consume());
        Assertions.assertTrue(tokenStream.isDone());
    }

    @Test
    void test_discard() {
        List<TypeParameterToken> tokens = List.of(
                TypeParameterToken.StaticToken.OPEN_ARRAY,
                TypeParameterToken.StaticToken.CLOSE_ARRAY,
                TypeParameterToken.StaticToken.OPEN_PARAMETER_LIST,
                TypeParameterToken.StaticToken.CLOSE_PARAMETER_LIST,
                TypeParameterToken.StaticToken.WILDCARD
        );
        TokenStream tokenStream = new TokenStream(tokens);

        Assertions.assertFalse(tokenStream.isDone());
        Assertions.assertTrue(tokenStream.hasNMoreTokens(5));
        Assertions.assertFalse(tokenStream.hasNMoreTokens(6));
        tokenStream.discard(2);

        Assertions.assertTrue(tokenStream.nextTokenIs(TypeParameterToken.StaticToken.OPEN_PARAMETER_LIST));
        Assertions.assertTrue(tokenStream.hasNMoreTokens(3));
        Assertions.assertFalse(tokenStream.hasNMoreTokens(4));
    }

    @Test
    void test_nextTokensAre() {
        List<TypeParameterToken> tokens = List.of(
                TypeParameterToken.StaticToken.OPEN_ARRAY,
                TypeParameterToken.StaticToken.CLOSE_ARRAY,
                TypeParameterToken.StaticToken.OPEN_PARAMETER_LIST,
                TypeParameterToken.StaticToken.CLOSE_PARAMETER_LIST,
                TypeParameterToken.StaticToken.WILDCARD
        );
        TokenStream tokenStream = new TokenStream(tokens);

        Assertions.assertTrue(tokenStream.nextTokensAre(
                TypeParameterToken.StaticToken.OPEN_ARRAY,
                TypeParameterToken.StaticToken.CLOSE_ARRAY,
                TypeParameterToken.StaticToken.OPEN_PARAMETER_LIST,
                TypeParameterToken.StaticToken.CLOSE_PARAMETER_LIST,
                TypeParameterToken.StaticToken.WILDCARD
        ));

        // fails equality check
        Assertions.assertFalse(tokenStream.nextTokensAre(
                new TypeParameterToken.Variable("bob")
        ));

        // fails length check
        Assertions.assertFalse(tokenStream.nextTokensAre(
                TypeParameterToken.StaticToken.WILDCARD,
                TypeParameterToken.StaticToken.WILDCARD,
                TypeParameterToken.StaticToken.WILDCARD,
                TypeParameterToken.StaticToken.WILDCARD,
                TypeParameterToken.StaticToken.WILDCARD,
                TypeParameterToken.StaticToken.WILDCARD
        ));
    }

}