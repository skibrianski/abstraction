package io.github.skibrianski.partial_interface.exception;

public class PartialInterfaceExtraneousTypeParameterException extends RuntimeException {
    public PartialInterfaceExtraneousTypeParameterException(String s) {
        super(s);
    }
    public PartialInterfaceExtraneousTypeParameterException(String s, Exception cause) {
        super(s, cause);
    }

}

