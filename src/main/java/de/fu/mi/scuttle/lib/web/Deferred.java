package de.fu.mi.scuttle.lib.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.fu.mi.scuttle.lib.ScuttleModule;

/**
 * Marks a module for deferred loading.
 * 
 * A {@link ScuttleModule}'s {@link ScuttleModule#loaded()} methods will be
 * invoked in the job queue instead of immediately if annotated with this
 * annotation. It will therefor not affect the startup time of tomcat.
 * 
 * A module annotated with this annotation can never be {@link Crucial}.
 * 
 * @author Julian Fleischer
 * @since 2011-11-20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Deferred {

}
