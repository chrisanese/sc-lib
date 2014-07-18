package de.fu.mi.scuttle.lib.web;

public class ConversionException extends Exception {

    private static final long serialVersionUID = -7238650356997534972L;

    public ConversionException() {
        super();
    }

    public ConversionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ConversionException(String arg0) {
        super(arg0);
    }

    public ConversionException(Throwable arg0) {
        super(arg0);
    }

}