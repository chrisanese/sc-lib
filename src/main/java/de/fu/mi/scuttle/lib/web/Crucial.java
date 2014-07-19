package de.fu.mi.scuttle.lib.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a module as being crucial for the whole application.
 * 
 * A module annotated with this annotation will create scuttle configuration
 * problems instead of warnings (like they normally do).
 * 
 * This annotation is incompatible with {@link Deferred}.
 * 
 * @author Julian Fleischer
 * @since 2011-11-20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Crucial {

}
