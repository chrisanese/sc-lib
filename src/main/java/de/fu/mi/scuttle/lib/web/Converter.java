package de.fu.mi.scuttle.lib.web;

/**
 * A Converter converts a string into something.
 * 
 * @author Julian Fleischer
 * 
 * @param <X>
 *            That something this converter will convert the string to.
 */
public interface Converter<X> {

    /**
     * Convert a String value into the destined type.
     * 
     * @param value
     *            The string value.
     * @return The value of the destined type.
     * @throws ConversionException
     *             If the value could not be converted to the desired type.
     */
    X convert(String value) throws ConversionException;
}