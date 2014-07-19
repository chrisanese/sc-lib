package de.fu.mi.scuttle.lib.web;

/**
 * 
 * @author Julian Fleischer
 * @since 2013-11-20
 */
public @interface ConfigItem {

    String key();

    String value();

    String comment();
}
