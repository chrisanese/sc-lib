package de.fu.mi.scuttle.lib.persistence;

import javax.persistence.NoResultException;

public interface TypedQuery<T> extends
        javax.persistence.TypedQuery<T>, Query {

    /**
     * Execute a SELECT query that returns a single untyped result.
     * 
     * In contrast to the method from javax.persistence this method does not
     * throw a {@link NoResultException}, but return null if no result is found.
     */
    @Override
    T getSingleResult();

}
