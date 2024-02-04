package io.github.skibrianski.abstraction;

public class AbstractionException extends RuntimeException {
    public AbstractionException(String s) {
        super(s);
    }
    public AbstractionException(String s, Exception cause) {
        super(s, cause);
    }


    public abstract static class NotCompletedException extends AbstractionException {
        public NotCompletedException(String s) {
            super(s);
        }
    }
    public static class MissingRequiredTypeParameter extends NotCompletedException {
        public MissingRequiredTypeParameter(String s) {
            super(s);
        }
    }
    public static class NoMethodWithMatchingName extends NotCompletedException {
        public NoMethodWithMatchingName(String s) {
            super(s);
        }
    }
    public static class ClashingArgumentTypeException extends NotCompletedException {
        public ClashingArgumentTypeException(String s) {
            super(s);
        }
    }
    public static class ClashingReturnTypeException extends NotCompletedException {
        public ClashingReturnTypeException(String s) {
            super(s);
        }
    }

    public static class ExtraneousTypeParameterException extends AbstractionException {
        public ExtraneousTypeParameterException(String s) {
            super(s);
        }
    }

    public static class MissingTypeParameterException extends AbstractionException {
        public MissingTypeParameterException(String s) {
            super(s);
        }
    }

    public static class UsageException extends AbstractionException {
        public UsageException(String s) {
            super(s);
        }
        public UsageException(String s, Exception cause) {
            super(s, cause);
        }
    }

    public static class TypeParameterViolatesBounds extends AbstractionException {
        public TypeParameterViolatesBounds(String s) {
            super(s);
        }
    }


}

