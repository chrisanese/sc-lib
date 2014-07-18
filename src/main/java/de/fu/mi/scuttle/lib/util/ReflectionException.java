package de.fu.mi.scuttle.lib.util;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-10-26
 */
public class ReflectionException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5043874579833615905L;

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(Throwable cause, String message) {
        super(message, cause);
    }

}
