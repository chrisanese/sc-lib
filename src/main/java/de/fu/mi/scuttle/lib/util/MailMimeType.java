package de.fu.mi.scuttle.lib.util;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-11-17
 */
public enum MailMimeType {

    PLAIN("text/plain"), HTML("text/html");

    private final String mimeType;

    MailMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
