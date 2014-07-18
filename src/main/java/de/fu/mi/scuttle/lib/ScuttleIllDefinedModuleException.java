package de.fu.mi.scuttle.lib;

public class ScuttleIllDefinedModuleException extends Exception {

    public ScuttleIllDefinedModuleException(
            final Class<? extends ScuttleModule> clazz, final String string) {
        super(string);
    }

    /**
     * Legacy serial version uid.
     */
    private static final long serialVersionUID = 7034499297130705351L;

}
