package de.fu.mi.scuttle.lib.util;

/**
 * A mail protocol supported by {@link Mail}.
 * 
 * @author Julian Fleischer
 * @since 2013-11-17
 */
public enum MailProtocol {
    SMTP("smtp"),
    SMTPS("smtps");

    private final String protocol;

    MailProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public static MailProtocol forName(final String protocol) {
        if (protocol == null) {
            return null;
        }
        return MailProtocol.valueOf(protocol.toLowerCase());
    }

    @Override
    public String toString() {
        return protocol;
    }
}