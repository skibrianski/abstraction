//package io.github.skibrianski.partial_interface;
//
//import io.github.skibrianski.partial_interface.internal.ClassType;
//import io.github.skibrianski.partial_interface.internal.IType;
//import io.github.skibrianski.partial_interface.internal.TypeVariable;
//
//import java.util.Map;
//
//public class TypeStringTokenizer {
//
//    private final String typeString;
//    private final Map<String, Class<?>> typeParameterMap;
//    private int pos = 0;
//
//    public TypeStringTokenizer(String typeString, Map<String, Class<?>> typeParameterMap) {
//        this.typeString = typeString;
//        this.typeParameterMap = typeParameterMap;
//    }
//
//    private TypeStringTokenizer(String typeString, int pos, Map<String, Class<?>> typeParameterMap) {
//        this(typeString, typeParameterMap);
//        this.pos = pos;
//    }
//
//    class Node {
//
//        private final String baseName;
//        private final Node[] typeArguments;
//        public Node(String baseName, Node[] typeArguments) {
//            this.baseName = baseName;
//            this.typeArguments = typeArguments;
//        }
//
//        public String baseName() {
//            return baseName;
//        }
//
//        public Node[] typeArguments() {
//            return typeArguments;
//        }
//    }
//
//    public IType parse() {
//        // strip off leading whitespace
//        while (typeString.substring(pos, pos + 1).isBlank()) {
//            pos++;
//        }
//
//        String variable = nextVariable();
//        char nextToken = typeString.length() == pos ? (char) -1 : typeString.charAt(pos);
//        if (nextToken == '<') {
//            int end = nextBalanced(pos + 1);
//            String argumentStrings =
//            // TODO: find next , and recurse until '>'
//        } else {
//            if (typeParameterMap.containsKey(variable)) {
//                return new TypeVariable(variable, typeParameterMap);
//            } else {
//                try {
//                    return new ClassType<>(Class.forName(variable), typeParameterMap);
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e); // TODO: something else probably
//                }
//            }
//        }
//    }
//
//    private int nextBalanced(int startPos) {
//        String haystack = typeString.substring(startPos);
//        int nestingLevel = 1;
//        int haystackPos = 0;
//        while (nestingLevel > 0) {
//            char next = haystack.charAt(haystackPos);
//            if (next == '<') {
//                nestingLevel++;
//            }
//            if (next == '>') {
//                nestingLevel--;
//            }
//            haystackPos++;
//        }
//        return haystackPos + startPos;
//    }
//
//    private String nextVariable() {
//        String[] parts = typeString.substring(pos).split("[<>,]", 2);
//        if (parts.length == 1) {
//            return null;
//        }
//        pos += parts[0].length();
//        return parts[0];
//    }
//}
//
