package de.fu.mi.scuttle.lib;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-10-24
 */
public class ScuttleNoPermissionException extends Exception {

    public static enum NoPermissionException {
        SESSION_USER_NOT_IN_DATABASE,
        USER_LACKS_PRIVILEGE,
        NO_SESSION
    }

    /**
     * legacy serial version uid
     */
    private static final long serialVersionUID = 4171344692601386928L;

    public ScuttleNoPermissionException(NoPermissionException type) {
        super(type.toString());
    }
}
