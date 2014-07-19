package de.fu.mi.scuttle.lib.util;

/**
 * Utility methods for working with paths.
 * 
 * @author Julian Fleischer
 */
public final class PathUtil {

    private PathUtil() {
    }

    /**
     * Parse a path given as string into its head component and its tail.
     * 
     * @param path
     *            The path to gather information about.
     * @return An array with two elements: The first one describing the first
     *         part of the path, the second one containing the rest of the path.
     *         If either of these ingredients do not exist, the empty string is
     *         contained. Neither the returned value nor one of its components
     *         will be null.
     */
    public static String[] pathInfo(String path) {
        if (path == null) {
            return new String[] { "", "" };
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        final String[] str = path.split("/+", 2);
        if (str.length == 1) {
            return new String[] { str[0], "" };
        } else if (str.length == 2) {
            return str;
        }
        return new String[] { path, "" };
    }

}
