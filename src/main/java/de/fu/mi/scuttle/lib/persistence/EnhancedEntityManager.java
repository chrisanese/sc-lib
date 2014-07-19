package de.fu.mi.scuttle.lib.persistence;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;

class EnhancedEntityManager implements EntityManager {

    private final javax.persistence.EntityManager em;

    public EnhancedEntityManager(final javax.persistence.EntityManager em) {
        this.em = em;
    }

    @Override
    public void clear() {
        em.clear();
    }

    @Override
    public void close() {
        if (isOpen()) {
            em.close();
        }
    }

    @Override
    public boolean contains(final Object arg0) {
        return em.contains(arg0);
    }

    @Override
    public Query createNamedQuery(final String arg0) {
        return new EnhancedQuery(em.createNamedQuery(arg0));
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String arg0,
            final Class<T> arg1) {
        return new EnhancedTypedQuery<>(em.createNamedQuery(arg0, arg1));
    }

    @Override
    public Query createNativeQuery(final String arg0) {
        return new EnhancedQuery(em.createNativeQuery(arg0));
    }

    @Override
    public Query createNativeQuery(final String arg0,
            @SuppressWarnings("rawtypes") final Class arg1) {
        return new EnhancedQuery(em.createNativeQuery(arg0, arg1));
    }

    @Override
    public Query createNativeQuery(final String arg0, final String arg1) {
        return new EnhancedQuery(em.createNativeQuery(arg0, arg1));
    }

    @Override
    public Query createQuery(final String arg0) {
        return new EnhancedQuery(em.createQuery(arg0));
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> arg0) {
        return new EnhancedTypedQuery<>(em.createQuery(arg0));
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String arg0, final Class<T> arg1) {
        return new EnhancedTypedQuery<>(em.createQuery(arg0, arg1));
    }

    @Override
    public void detach(final Object arg0) {
        em.detach(arg0);
    }

    @Override
    public <T> T find(final Class<T> arg0, final Object arg1) {
        return em.find(arg0, arg1);
    }

    @Override
    public <T> T find(final Class<T> arg0, final Object arg1,
            final Map<String, Object> arg2) {
        return em.find(arg0, arg1, arg2);
    }

    @Override
    public <T> T find(final Class<T> arg0, final Object arg1,
            final LockModeType arg2) {
        return em.find(arg0, arg1, arg2);
    }

    @Override
    public <T> T find(final Class<T> arg0, final Object arg1,
            final LockModeType arg2,
            final Map<String, Object> arg3) {
        return em.find(arg0, arg1, arg2, arg3);
    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public Object getDelegate() {
        return em.getDelegate();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return em.getEntityManagerFactory();
    }

    @Override
    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    @Override
    public LockModeType getLockMode(final Object arg0) {
        return em.getLockMode(arg0);
    }

    @Override
    public Metamodel getMetamodel() {
        return em.getMetamodel();
    }

    @Override
    public Map<String, Object> getProperties() {
        return em.getProperties();
    }

    @Override
    public <T> T getReference(final Class<T> arg0, final Object arg1) {
        return em.getReference(arg0, arg1);
    }

    @Override
    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }

    @Override
    public boolean isOpen() {
        try {
            return em.isOpen();
        } catch (final Exception exc) {
            return false;
        }
    }

    @Override
    public void joinTransaction() {
        em.joinTransaction();
    }

    @Override
    public void lock(final Object object, final LockModeType arg1) {
        em.lock(object, arg1);
    }

    @Override
    public void lock(final Object arg0, final LockModeType arg1,
            final Map<String, Object> arg2) {
        em.lock(arg0, arg1, arg2);
    }

    @Override
    public <T> T merge(final T object) {
        return em.merge(object);
    }

    @Override
    public void persistAll(final Object... objects) {
        EntityTransaction t = getTransaction();
        if (t.isActive()) {
            t = null;
        } else {
            t.begin();
        }
        for (final Object object : objects) {
            persist(object);
        }
        if (t != null) {
            t.commit();
        }
    }

    @Override
    public void persist(final Object object) {
        if (object instanceof UuidEntity) {
            final UuidEntity<?> uuidObject = (UuidEntity<?>) object;
            if (uuidObject.getUuid() == null) {
                uuidObject.setUuid(UUID.randomUUID().toString());
            }
        }
        if (object instanceof CreationTimeEntity) {
            final CreationTimeEntity<?> timeObject = (CreationTimeEntity<?>) object;
            if (timeObject.getCreationTime() == 0) {
                timeObject.setCreationTime(System.currentTimeMillis());
            }
        }
        if (object instanceof LastModificationTimeEntity) {
            final LastModificationTimeEntity<?> timeObject = (LastModificationTimeEntity<?>) object;
            timeObject.setLastModificationTime(System.currentTimeMillis());
        }

        final EntityTransaction transaction = getTransaction();
        boolean commitTransaction = false;
        if (!transaction.isActive()) {
            transaction.begin();
            commitTransaction = true;
        }
        em.persist(object);
        if (commitTransaction) {
            transaction.commit();
        }
    }

    @Override
    public void removeAll(final Object... objects) {
        EntityTransaction t = getTransaction();
        if (t.isActive()) {
            t = null;
        } else {
            t.begin();
        }
        for (final Object object : objects) {
            em.remove(object);
        }
        if (t != null) {
            t.commit();
        }
    }

    @Override
    public void refresh(final Object object) {
        em.refresh(object);
    }

    @Override
    public void refresh(final Object object, final Map<String, Object> options) {
        em.refresh(object, options);
    }

    @Override
    public void refresh(final Object object, final LockModeType lockModeType) {
        em.refresh(object, lockModeType);
    }

    @Override
    public void refresh(final Object object, final LockModeType lockModeType,
            final Map<String, Object> options) {
        em.refresh(object, lockModeType, options);
    }

    @Override
    public void remove(final Object arg0) {
        final EntityTransaction transaction = getTransaction();
        boolean commitTransaction = false;
        if (!transaction.isActive()) {
            transaction.begin();
            commitTransaction = true;
        }
        em.remove(arg0);
        if (commitTransaction) {
            transaction.commit();
        }
    }

    @Override
    public void setFlushMode(final FlushModeType arg0) {
        em.setFlushMode(arg0);
    }

    @Override
    public void setProperty(final String arg0, final Object arg1) {
        em.setProperty(arg0, arg1);
    }

    @Override
    public <T> T unwrap(final Class<T> arg0) {
        return em.unwrap(arg0);
    }

    @Override
    public <T> List<T> findAll(final Class<T> entityClass) {
        final CriteriaBuilder b = em.getCriteriaBuilder();
        final CriteriaQuery<T> cq = b.createQuery(entityClass);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public <T> List<T> findAll(final Class<T> entityClass, final int limit) {
        final CriteriaBuilder b = em.getCriteriaBuilder();
        final CriteriaQuery<T> cq = b.createQuery(entityClass);
        final javax.persistence.TypedQuery<T> q = em.createQuery(cq);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public <T> List<T> findAll(final Class<T> entityClass, final int limit,
            final int offset) {
        final CriteriaBuilder b = em.getCriteriaBuilder();
        final CriteriaQuery<T> cq = b.createQuery(entityClass);
        final javax.persistence.TypedQuery<T> q = em.createQuery(cq);
        q.setFirstResult(limit);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public String escapeLike(final String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%")
                .replace("_", "\\_");
    }

    @Override
    public List<?> query(final String query) {
        final javax.persistence.Query q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public <T> List<T> query(final String query, final Class<T> clazz) {
        final javax.persistence.TypedQuery<T> q = em.createQuery(query, clazz);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> mapQuery(final String query) {
        final javax.persistence.Query q = em.createQuery(query);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        return q.getResultList();
    }

    @Override
    public List<?> namedQuery(final String namedQuery) {
        final javax.persistence.Query q = em.createNamedQuery(namedQuery);
        return q.getResultList();
    }

    @Override
    public <T> List<T> namedQuery(final String query, final Class<T> clazz) {
        final javax.persistence.TypedQuery<T> q = em.createNamedQuery(query,
                clazz);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> nativeQuery(final String nativeQuery) {
        final javax.persistence.Query q = em.createNativeQuery(nativeQuery);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        return q.getResultList();
    }

    @Override
    public Object querySingle(final String query) {
        try {
            final javax.persistence.Query q = em.createQuery(query);
            return q.getSingleResult();
        } catch (final NoResultException exc) {
            return null;
        }
    }

    @Override
    public <T> T querySingle(final String query, final Class<T> clazz) {
        try {
            final javax.persistence.TypedQuery<T> q = em.createQuery(query,
                    clazz);
            return q.getSingleResult();
        } catch (final NoResultException exc) {
            return null;
        }
    }

    @Override
    public Object namedQuerySingle(final String namedQuery) {
        try {
            final javax.persistence.Query q = em.createNamedQuery(namedQuery);
            return q.getSingleResult();
        } catch (final NoResultException exc) {
            return null;
        }
    }

    @Override
    public <T> T namedQuerySingle(final String query, final Class<T> clazz) {
        try {
            final javax.persistence.TypedQuery<T> q = em.createNamedQuery(
                    query,
                    clazz);
            return q.getSingleResult();
        } catch (final NoResultException exc) {
            return null;
        }
    }

    @Override
    public Object nativeQuerySingle(final String nativeQuery) {
        try {
            final javax.persistence.Query q = em.createNativeQuery(nativeQuery);
            return q.getSingleResult();
        } catch (final NoResultException exc) {
            return null;
        }
    }

    @Override
    public List<?> query(final String query, final int limit) {
        final javax.persistence.Query q = em.createQuery(query);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> mapQuery(final String query,
            final int limit) {
        final javax.persistence.Query q = em.createQuery(query);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public <T> List<T> query(final Class<T> clazz, final String query,
            final int limit) {
        final javax.persistence.TypedQuery<T> q = em.createQuery(query, clazz);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<?> namedQuery(final String namedQuery, final int limit) {
        final javax.persistence.Query q = em.createNamedQuery(namedQuery);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public <T> List<T> namedQuery(final Class<T> clazz,
            final String namedQuery, final int limit) {
        final javax.persistence.TypedQuery<T> q = em.createNamedQuery(
                namedQuery,
                clazz);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> nativeQuery(final String nativeQuery,
            final int limit) {
        final javax.persistence.Query q = em.createNativeQuery(nativeQuery);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<?> query(final String query, final int offset, final int limit) {
        final javax.persistence.Query q = em.createQuery(query);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> mapQuery(final String query,
            final int offset,
            final int limit) {
        final javax.persistence.Query q = em.createQuery(query);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public <T> List<T> query(final Class<T> clazz, final String query,
            final int offset, final int limit) {
        final javax.persistence.TypedQuery<T> q = em.createQuery(query, clazz);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<?> namedQuery(final String namedQuery, final int offset,
            final int limit) {
        final javax.persistence.Query q = em.createNamedQuery(namedQuery);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public <T> List<T> namedQuery(final Class<T> clazz,
            final String namedQuery,
            final int offset, final int limit) {
        final javax.persistence.TypedQuery<T> q = em.createNamedQuery(
                namedQuery,
                clazz);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> nativeQuery(final String nativeQuery,
            final int offset, final int limit) {
        final javax.persistence.Query q = em.createNativeQuery(nativeQuery);
        q.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    // The following methods are new in JPA 2.1

    @Override
    public <T> EntityGraph<T> createEntityGraph(final Class<T> entity) {
        return em.createEntityGraph(entity);
    }

    @Override
    public EntityGraph<?> createEntityGraph(final String entity) {
        return em.createEntityGraph(entity);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(
            final String name) {
        return em.createNamedStoredProcedureQuery(name);
    }

    @Override
    public Query createQuery(
            @SuppressWarnings("rawtypes") final CriteriaUpdate criteriaUpdate) {
        return new EnhancedQuery(em.createQuery(criteriaUpdate));
    }

    @Override
    public Query createQuery(
            @SuppressWarnings("rawtypes") final CriteriaDelete criteriaDelete) {
        return new EnhancedQuery(em.createQuery(criteriaDelete));
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String name) {
        return em.createStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String name,
            @SuppressWarnings("rawtypes") final Class... arg1) {
        return em.createStoredProcedureQuery(name, arg1);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String arg0,
            final String... arg1) {
        return em.createStoredProcedureQuery(arg0, arg1);
    }

    @Override
    public EntityGraph<?> getEntityGraph(final String entity) {
        return em.getEntityGraph(entity);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> arg0) {
        return em.getEntityGraphs(arg0);
    }

    @Override
    public boolean isJoinedToTransaction() {
        return em.isJoinedToTransaction();
    }

    @Override
    public int removeAll(final Class<?> entityClass) {
        final boolean isActive = em.getTransaction().isActive();
        if (!isActive) {
            em.getTransaction().begin();
        }
        final CriteriaBuilder b = em.getCriteriaBuilder();
        final int count = em.createQuery(b.createCriteriaDelete(entityClass))
                .executeUpdate();
        if (!isActive) {
            em.getTransaction().commit();
        }
        return count;
    }

    @Override
    public void evict(final Class<?> clazz) {
        getEntityManagerFactory().getCache().evict(clazz);
    }

    @Override
    public void evictAll() {
        getEntityManagerFactory().getCache().evictAll();
    }
}
