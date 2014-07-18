package de.fu.mi.scuttle.lib;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-10-18
 */
public class ScuttleConnectivityException extends Exception {

    /**
     * Legacy serial version UID.
     */
    private static final long serialVersionUID = -8903174934437006176L;

    ScuttleConnectivityException(final Exception exc) {
        super("Could not connect to the database.", exc);
    }

    ScuttleConnectivityException(final String dbVersion, final String value) {
        super(String.format("DB-Versions mismatch. Required: %s, Found: %s",
                dbVersion, value));
    }

    ScuttleConnectivityException(final String dbVersion) {
        super(
                String.format(
                        "No DB-Version Information could be found in the database (expected %s).",
                        dbVersion));
    }
}
