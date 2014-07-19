package de.fu.mi.scuttle.lib.web;

import de.fu.mi.scuttle.lib.ScuttleModule;
import de.fu.mi.scuttle.lib.ScuttleNoPermissionException;
import de.fu.mi.scuttle.lib.ScuttleUpdateVisitor;
import de.fu.mi.scuttle.lib.ScuttleUpdater;
import de.fu.mi.scuttle.lib.persistence.DbProvider;
import de.fu.mi.scuttle.lib.persistence.EntityManager;

/**
 * A ScuttleServlet provides handlers which can communicate to each other via
 * {@link #getModule(Class)} and it is a {@link DbProvider}.
 * 
 * @author Julian Fleischer
 */
public interface ScuttleServlet extends DbProvider, ScuttleSimpleServlet {

    void check(ScuttleRequest request, String privilege)
            throws ScuttleNoPermissionException;

    <T extends ScuttleModule> T getModule(Class<T> clazz);

    /**
     * Updates this servlets internal state while online, in a thread safe
     * manner.
     * 
     * @param updater
     *            The updater that will update the internal state.
     */
    void update(ScuttleUpdater updater);

    /**
     * Like {@link #update(ScuttleUpdater)}, but with a ScuttleUpdateVisitor
     * that applies only to certain Handler classes.
     * 
     * @param updater
     *            The updater that will update the internal state.
     */
    void update(ScuttleUpdateVisitor<?> updater);

    /**
     * Retrieves an EntityManager for working with a database, thread-safe.
     */
    EntityManager db();

}