package io.github.skibrianski.partial_interface.exception;

public class PartialInterfaceMissingTypeParameterException extends RuntimeException {
    public PartialInterfaceMissingTypeParameterException(String s) {
        super(s);
    }
    public PartialInterfaceMissingTypeParameterException(String s, Exception cause) {
        super(s, cause);
    }

}

