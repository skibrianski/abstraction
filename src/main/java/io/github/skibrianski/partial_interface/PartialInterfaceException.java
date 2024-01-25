package io.github.skibrianski.partial_interface;

// TODO: nest subclasses in here.
public class PartialInterfaceException extends RuntimeException {
    public PartialInterfaceException(String s) {
        super(s);
    }
    public PartialInterfaceException(String s, Exception cause) {
        super(s, cause);
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


}

