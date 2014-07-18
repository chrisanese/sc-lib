package de.fu.mi.scuttle.lib;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-10-18
 */
public class ScuttleHandlerInitializationException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7149582272303553680L;

    private final String mountPoint;
    private final String className;

    public ScuttleHandlerInitializationException(Exception exc, String name,
            String className) {
        super(exc);
        this.mountPoint = name;
        this.className = className;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public String getClassName() {
        return className;
    }
}
