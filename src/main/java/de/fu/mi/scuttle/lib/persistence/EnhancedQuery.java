package de.fu.mi.scuttle.lib.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;

class EnhancedQuery implements Query {

    private final javax.persistence.Query query;

    EnhancedQuery(javax.persistence.Query Query) {
        this.query = Query;
    }

    @Override
    public List<?> getResultList() {
        return query.getResultList();
    }

    @Override
    public Object getSingleResult() {
        try {
            return query.getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }
    }

    @Override
    public int executeUpdate() {
        return query.executeUpdate();
    }

    @Override
    public EnhancedQuery setMaxResults(int maxResult) {
        return this;
    }

    @Override
    public int getMaxResults() {
        return query.getMaxResults();
    }

    @Override
    public EnhancedQuery setFirstResult(int startPosition) {
        query.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return query.getFirstResult();
    }

    @Override
    public EnhancedQuery setHint(String hintName, Object value) {
        query.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return query.getHints();
    }

    @Override
    public <T> EnhancedQuery setParameter(Parameter<T> param, T value) {
        query.setParameter(param, value);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(Parameter<Calendar> param,
            Calendar value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(Parameter<Date> param,
            Date value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(String name, Object value) {
        query.setParameter(name, value);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(String name, Calendar value,
            TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(String name, Date value,
            TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(int position, Object value) {
        query.setParameter(position, value);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(
            int position, Calendar value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public EnhancedQuery setParameter(
            int position, Date value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return query.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return query.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return query.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return query.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return query.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return query.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return query.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return query.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return query.getParameterValue(position);
    }

    @Override
    public EnhancedQuery setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    @Override
    public EnhancedQuery setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return query.unwrap(cls);
    }
}
