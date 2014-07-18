package de.fu.mi.scuttle.lib.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.fu.mi.scuttle.lib.ScuttleModule;

/**
 * Defines the path at which a {@link ScuttleModule} is to be mounted.
 * 
 * This may not be a subpath (i.e. it may not contain a slash), since the module
 * structure is pretty flat (which is intended).
 * 
 * @author Julian Fleischer
 * @since 2011-11-16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MountPoint {

    String value();
}
