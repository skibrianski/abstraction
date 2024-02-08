package io.github.skibrianski.abstraction;


public interface TypeParameterToken {

    String asString();

    static TypeParameterToken of(String string) {
        for (StaticToken staticToken : StaticToken.values()) {
            if (string.equals(staticToken.asString())) {
                return staticToken;
            }
        }
        return new Variable(string);
    }

    enum StaticToken implements TypeParameterToken {
        OPEN_PARAMETER_LIST("<"),
        CLOSE_PARAMETER_LIST(">"),
        OPEN_ARRAY("["),
        CLOSE_ARRAY("]"),
        LIST_SEPARATOR(","),
        EXTENDS("extends"),
        SUPER("super"),
        WILDCARD("?"),
        VARARGS("...");

        private final String string;

        StaticToken(String string) {
            this.string = string;
        }

        @Override
        public String asString() {
            return string;
        }
    }

    class Variable implements TypeParameterToken {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }
        @Override
        public String asString() {
            return name;
        }

        @Override
        public String toString() {
            return "Variable{" + name + "}";
        }
    }
}

