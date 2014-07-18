package de.fu.mi.scuttle.lib.web;

import de.fu.mi.scuttle.lib.ScuttleModule;

/**
 * A Request that is processed by a {@link ScuttleModule}.
 * 
 * @author Julian Fleischer
 */
public interface ScuttleRequest {

    public static enum RequestMethod {
        GET, POST, PUT, UNKNOWN
    }

    /**
     * Check whether the named param is set or not.
     * 
     * @param name
     *            The name of the parameter in the request.
     * @return true iff the parameter is not set.
     */
    boolean isNull(String name);

    /**
     * Retrieve the value for a given header.
     * 
     * @param name
     *            The name of the header
     * @return The value of the header or null if the header was not set in this
     *         request.
     * @since 2013-10-18
     */
    String getHeader(String name);

    /**
     * Return the string value of the named parameter.
     * 
     * @param name
     *            The name of the parameter in the request.
     * @return The parameters as a string or null if is is not set.
     */
    String get(String name);

    /**
     * Return the value of the named parameter or a default value if the
     * parameter is not set.
     * 
     * @param name
     *            The name of the parameter in the request.
     * @param defaultValue
     *            The default value to be used if the string value was either
     *            null or could not be converted to a value of the type of the
     *            defaultValue.
     * @return The value converted to the type of the default value.
     */
    <T> T get(String name, T defaultValue);

    /**
     * Return the value of the named parameter.
     * 
     * @param name
     *            The name of the parameter in the request.
     * @param clazz
     *            The target type.
     * @return The value converted to the target type.
     * @throws ConversionException
     *             If the conversion failed.
     */
    <T> T get(String name, Class<T> clazz) throws ConversionException;

    /**
     * Return the requested path relative to the handler.
     * 
     * If you access <code>scuttle/<handler>/some/thing</code>, the path will be
     * <code>some/thing</code>.
     * 
     * @return The path relative to the handlers mount point, without a leading
     *         slash. This may be the empty string.
     */
    String getPath();

    /**
     * Check whether this requests was sent by a UA that accepts a gzipped
     * response.
     * 
     * @return Whether it does or not.
     */
    boolean acceptsGzip();

    /**
     * Retrieve the session associated with the current request.
     * 
     * @return The session.
     */
    ScuttleSession getSession();

    /**
     * Retrieve the HTTP method that was used to make this request.
     * 
     * @return GET, POST, ...
     */
    RequestMethod getRequestMethod();
}
