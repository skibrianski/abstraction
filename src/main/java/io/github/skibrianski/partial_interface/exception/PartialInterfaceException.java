package io.github.skibrianski.partial_interface.exception;

// TODO: nest subclasses in here.
public class PartialInterfaceException extends RuntimeException {
    public PartialInterfaceException(String s) {
        super(s);
    }
    public PartialInterfaceException(String s, Exception cause) {
        super(s, cause);
    }

}

