package de.fu.mi.scuttle.lib.web;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-09-22
 */
public interface ScuttleSession {

    String getString(String key);

    Object get(String key);

    <T> T get(String key, T defaultValue);

    void put(String key, Object value);

    void clear();

    void remove(String string);
}
