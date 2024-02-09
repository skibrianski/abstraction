package io.github.skibrianski.abstraction;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenStream {

    private final List<TypeParameterToken> tokens;
    private int pos = 0;
    public TokenStream(List<TypeParameterToken> tokens) {
        this.tokens = tokens;
    }

    public TokenStream(String typeString) {
        this(tokenize(typeString));
    }

    private static List<TypeParameterToken> tokenize(String typeString) {
        // surround each punctuation-based token with spaces to aid tokenization
        for (TypeParameterToken.StaticToken token : TypeParameterToken.StaticToken.values()) {
            typeString = typeString.replaceAll(
                    "\\Q" + token.asString() + "\\E",
                    " " + token.asString() + " "
            );
        }
        return Arrays.stream(typeString.trim().split("\\s+"))
                .map(TypeParameterToken::of)
                .collect(Collectors.toList());
    }

    public TypeParameterToken consume() {
        TypeParameterToken token = tokens.get(pos);
        pos++;
        return token;
    }

    public void discard(int n) {
        pos += n;
    }

    public boolean isDone() {
        return pos >= tokens.size();
    }

    public boolean hasNMoreTokens(int n) {
        return pos + n <= tokens.size();
    }

    public boolean nextTokenIsVariable() {
        return !isDone() && tokens.get(pos) instanceof TypeParameterToken.Variable;
    }

    public boolean nextTokenIs(TypeParameterToken nextToken) {
        return !isDone() && tokens.get(pos).equals(nextToken);
    }

    public boolean nextTokensAre(TypeParameterToken... nextTokens) {
        if (!hasNMoreTokens(nextTokens.length)) {
            return false;
        }
        for (int n = 0; n < nextTokens.length; n++) {
            if (!nextTokens[n].equals(tokens.get(pos + n))) {
                return false;
            }
        }
        return true;
    }

}

