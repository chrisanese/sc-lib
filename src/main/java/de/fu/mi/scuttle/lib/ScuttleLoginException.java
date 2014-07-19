package de.fu.mi.scuttle.lib;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-10-24
 */
public class ScuttleLoginException extends Exception {

    public static enum LoginException {
        NO_PASSWORD_GIVEN,
        PASSWORDS_DO_NOT_MATCH,
        UNKNOWN_USER
    }

    /**
     * 
     */
    private static final long serialVersionUID = 8097559695328736851L;

    public ScuttleLoginException(LoginException e) {
        super(e.toString());
    }

}
