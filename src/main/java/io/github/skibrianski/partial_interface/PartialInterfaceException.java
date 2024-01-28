package io.github.skibrianski.partial_interface;

public class PartialInterfaceException extends RuntimeException {
    public PartialInterfaceException(String s) {
        super(s);
    }
    public PartialInterfaceException(String s, Exception cause) {
        super(s, cause);
    }

    public static class ClashingReturnTypeException extends PartialInterfaceException {
        public ClashingReturnTypeException(String s) {
            super(s);
        }

    }
    public static class ExtraneousTypeParameterException extends PartialInterfaceException {
        public ExtraneousTypeParameterException(String s) {
            super(s);
        }
    }

    public static class MissingTypeParameterException extends PartialInterfaceException {
        public MissingTypeParameterException(String s) {
            super(s);
        }
    }

    public static class NotCompletedException extends PartialInterfaceException {
        public NotCompletedException(String s) {
            super(s);
        }
    }

    public static class UsageException extends PartialInterfaceException {
        public UsageException(String s) {
            super(s);
        }
        public UsageException(String s, Exception cause) {
            super(s, cause);
        }
    }

    public static class TypeParameterViolatesBounds extends PartialInterfaceException {
        public TypeParameterViolatesBounds(String s) {
            super(s);
        }
    }


}

