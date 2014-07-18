package de.fu.mi.scuttle.lib.persistence;

/**
 * Use this class to wrap a javax.persistence.EntityManager into a
 * net.scravy.persistence.EntityManager.
 * 
 * A net.scravy.persistence.EntityManager offers some shorthand methods for
 * working with a JPA database.
 * 
 * @author Julian Fleischer
 * @since 1.0
 * 
 * @see EntityManager
 */
public class EntityManagerWrapper {

    /**
     * Wraps a javax.persistence.EntityManager into an {@link EntityManager},
     * which allows for some specialies functions for working with a JPA
     * database.
     * 
     * @param em
     *            The javax.persistence.EntityManager.
     * @return A net.scravy.persistence.EntityManager.
     * @since 1.0
     */
    public static EntityManager wrap(javax.persistence.EntityManager em) {
        return new EnhancedEntityManager(em);
    }

}
