package io.github.skibrianski.partial_interface.exception;

public class PartialInterfaceUsageException extends PartialInterfaceException {
    public PartialInterfaceUsageException(String s) {
        super(s);
    }
    public PartialInterfaceUsageException(String s, Exception cause) {
        super(s, cause);
    }
}

