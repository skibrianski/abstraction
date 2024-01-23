package io.github.skibrianski.partial_interface.exception;

public class PartialInterfaceException extends RuntimeException {
    public PartialInterfaceException(String s) {
        super(s);
    }
    public PartialInterfaceException(String s, Exception cause) {
        super(s, cause);
    }

}

