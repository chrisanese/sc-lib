package de.fu.mi.scuttle.lib.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.NamedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * An EntityManager as in JPA 2.0, but with some convenience (shorthand)
 * methods. It allows for drop-in replacement. Use {@link EntityManagerWrapper}
 * to wrap an existing EntityManager in order to use the functionality described
 * here.
 * 
 * <h2>Additional methods</h2>
 * 
 * <dl>
 * 
 * <dt>findAll(...)</dt>
 * <dd>
 * <ul>
 * <li>{@link #findAll(Class)}</li>
 * <li>{@link #findAll(Class, int)}</li>
 * <li>{@link #findAll(Class, int, int)}</li>
 * </ul>
 * </dd>
 * 
 * <dt>query(...)</dt>
 * <dd>
 * <ul>
 * <li>{@link #query(String)}</li>
 * <li>{@link #query(String, Class)}</li>
 * <li>{@link #query(String, int)}</li>
 * <li>{@link #query(Class, String, int)}</li>
 * <li>{@link #query(String, int, int)}</li>
 * <li>{@link #query(Class, String, int, int)}</li>
 * <li>{@link #querySingle(String)}</li>
 * <li>{@link #querySingle(String, Class)}</li>
 * </ul>
 * </dd>
 * 
 * <dt>mapQuery(...)</dt>
 * <dd>
 * <ul>
 * <li>{@link #mapQuery(String)}</li>
 * <li>{@link #mapQuery(String, int)}</li>
 * <li>{@link #mapQuery(String, int, int)}</li>
 * </ul>
 * </dd>
 * 
 * <dt>namedQuery(...)</dt>
 * <dd>
 * <ul>
 * <li>{@link #namedQuery(String)}</li>
 * <li>{@link #namedQuery(String, Class)}</li>
 * <li>{@link #namedQuery(String, int)}</li>
 * <li>{@link #namedQuery(Class, String, int)}</li>
 * <li>{@link #namedQuery(String, int, int)}</li>
 * <li>{@link #namedQuery(Class, String, int, int)}</li>
 * <li>{@link #namedQuerySingle(String)}</li>
 * <li>{@link #namedQuerySingle(String, Class)}</li>
 * </ul>
 * </dd>
 * 
 * <dt>nativeQuery(...)</dt>
 * <dd>
 * <ul>
 * <li>{@link #nativeQuery(String)}</li>
 * <li>{@link #nativeQuery(String, int)}</li>
 * <li>{@link #nativeQuery(String, int, int)}</li>
 * <li>{@link #nativeQuerySingle(String)}</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 * 
 * <h2>Notable differences</h2>
 * 
 * <h3>{@link #close()}</h3> Closing an already closed EntityManager will not
 * throw an exception.
 * 
 * <h3>{@link #isOpen()}</h3> Some JPA implementations allow you to call
 * isOpen() only once, and may throw an exception. This method is guaranteed to
 * always return true or false and never throw an exception.
 * 
 * <h3>{@link #persist(Object)}</h3> If there is no ongoing transaction, persist
 * will create one and commit it immediately.
 * 
 * @author Julian Fleischer
 * @since 1.0
 */
public interface EntityManager extends javax.persistence.EntityManager {

    /**
     * Obtain a query from a {@link NamedQuery} annotation on an entity.
     */
    @Override
    Query createNamedQuery(String name);

    /**
     * Obtain a typed query from a {@link NamedQuery} annotation on an entity.
     */
    @Override
    <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass);

    /**
     * Obtain a typed query from a {@link CriteriaQuery}. Use
     * {@link #getCriteriaBuilder()} in order to create a {@link CriteriaQuery}.
     */
    @Override
    <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery);

    /**
     * Create a JPQL-Query.
     */
    @Override
    Query createQuery(String qlString);

    /**
     * Create a typed JPQL-Query.
     */
    @Override
    <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass);

    /**
     * Find an object in the database by its primary key.
     * 
     * @return The object with the given primary key or null, if no such object
     *         exists.
     */
    @Override
    <T> T find(Class<T> entityClass, Object primaryKey);

    /**
     * Find all objects in the database which belong to the given entityClass.
     * 
     * @param entityClass
     *            The class to be search for.
     * @return A list of entities (an empty list if none are found).
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * Find all objects in the database which belong to the given entityClass,
     * upto the specified limit.
     * 
     * @param entityClass
     *            The class to be search for.
     * @param limit
     *            The maximum number of results.
     * 
     * @return A list of entities (an empty list if none are found).
     */
    <T> List<T> findAll(Class<T> entityClass, int limit);

    /**
     * Find all objects in the database which belong to the given entityClass,
     * upto the specified limit, starting at the specified offset.
     * 
     * @param entityClass
     *            The class to be search for.
     * @param limit
     *            The maximum number of results.
     * @param offset
     *            The offset.
     * 
     * @return A list of entities (an empty list if none are found).
     */
    <T> List<T> findAll(Class<T> entityClass, int limit, int offset);

    /**
     * Returns a {@link CriteriaBuilder} which can be used to create a
     * {@link CriteriaQuery}.
     */
    @Override
    CriteriaBuilder getCriteriaBuilder();

    /**
     * Escapes a string for use in a JPA-LIKE-statement.
     * 
     * @param value
     *            The string.
     * @return The string with LIKE-special characters (such as *) escaped.
     */
    String escapeLike(String value);

    /**
     * Checks whether this EntiyManager is open.
     * 
     * This method differs from the original method in the following: If the
     * entity manager is already closed, no exception is thrown, but false is
     * returned.
     * 
     * @see javax.persistence.EntityManager#isOpen()
     */
    @Override
    boolean isOpen();

    /**
     * Closes this EntityManager. If the entity manager is already closed, this
     * method will do nothing.
     */
    @Override
    void close();

    /**
     * Make an instance managed and persistent. If there is no ongoing
     * transaction, persist will create one and commit it immediately.
     * 
     * @param entity
     *            The entity instance
     */
    @Override
    void persist(Object entity);

    /**
     * Persists many entities at once. This is done using a transaction which
     * may fail, in which case none of the entities are persisted.
     * 
     * @param entities
     *            The entities to persist.
     */
    void persistAll(Object... entities);

    @Override
    void remove(Object entity);

    void removeAll(Object... entities);

    int removeAll(Class<?> entityClass);

    /**
     * Immediately executes a {@link Query}.
     * <p>
     * Equivalent to:
     * </p>
     * 
     * <pre>
     * Query q = entityManager.createQuery(query);
     * q.getResultList();
     * </pre>
     * 
     * @param query
     *            A JPQL query.
     * @return A list of results.
     */
    List<?> query(String query);

    /**
     * Immediately executes a {@link TypedQuery}.
     * <p>
     * Equivalent to:
     * </p>
     * 
     * <pre>
     * TypedQuery&lt;Example> = entityManager.createTypedQuery(query, Example.class);
     * q.getResultList();
     * </pre>
     * 
     * @param query
     *            A JPQL query.
     * @param clazz
     *            The expected result type of the query (for example en entity
     *            class).
     * @return A list of results, each of type T.
     */
    <T> List<T> query(String query, Class<T> clazz);

    List<Map<String, Object>> mapQuery(String query);

    /**
     * Immediately executes a {@link NamedQuery}.
     * <p>
     * Equivalent to:
     * </p>
     * 
     * <pre>
     * Query q = entityManager.createNamedQuery(query);
     * q.getResultList();
     * </pre>
     * 
     * @param namedQuery
     *            The name of a named query.
     * @return A list of results.
     */
    List<?> namedQuery(String namedQuery);

    /**
     * Immediately executes a {@link NamedQuery}.
     * <p>
     * Equivalent to:
     * </p>
     * 
     * <pre>
     * TypedQuery&lt;Example> = entityManager.createNamedQuery(query, Example.class);
     * q.getResultList();
     * </pre>
     * 
     * @param namedQuery
     *            The name of a named query.
     * @param clazz
     *            The expected result type of the query (for example an entity
     *            class).
     * @return A list of results, each of type T.
     */
    <T> List<T> namedQuery(String namedQuery, Class<T> clazz);

    List<Map<String, Object>> nativeQuery(String nativeQuery);

    Object querySingle(String query);

    <T> T querySingle(String query, Class<T> clazz);

    Object namedQuerySingle(String namedQuery);

    <T> T namedQuerySingle(String query, Class<T> clazz);

    Object nativeQuerySingle(String nativeQuery);

    List<?> query(String query, int limit);

    List<Map<String, Object>> mapQuery(String query, int limit);

    <T> List<T> query(Class<T> clazz, String query, int limit);

    List<?> namedQuery(String namedQuery, int limit);

    <T> List<T> namedQuery(Class<T> clazz, String query, int limit);

    List<Map<String, Object>> nativeQuery(
            String nativeQuery, int limit);

    List<?> query(String query, int offset, int limit);

    List<Map<String, Object>> mapQuery(
            String nativeQuery, int offset, int limit);

    <T> List<T> query(Class<T> clazz, String query, int offset, int limit);

    List<?> namedQuery(String namedQuery, int offset, int limit);

    <T> List<T> namedQuery(Class<T> clazz, String query, int offset, int limit);

    List<Map<String, Object>> nativeQuery(
            String nativeQuery, int offset, int limit);

    void evict(Class<?> clazz);

    void evictAll();

}
