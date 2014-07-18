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

class EnhancedTypedQuery<T> implements TypedQuery<T> {

    private final javax.persistence.TypedQuery<T> typedQuery;

    EnhancedTypedQuery(javax.persistence.TypedQuery<T> typedQuery) {
        this.typedQuery = typedQuery;
    }

    @Override
    public List<T> getResultList() {
        return typedQuery.getResultList();
    }

    @Override
    public T getSingleResult() {
        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }
    }

    @Override
    public EnhancedTypedQuery<T> setFirstResult(int arg0) {
        typedQuery.setFirstResult(0);
        return null;
    }

    @Override
    public EnhancedTypedQuery<T> setFlushMode(FlushModeType arg0) {
        typedQuery.setFlushMode(arg0);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setHint(String arg0, Object arg1) {
        typedQuery.setHint(arg0, arg1);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setLockMode(LockModeType arg0) {
        typedQuery.setLockMode(arg0);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setMaxResults(int arg0) {
        typedQuery.setMaxResults(arg0);
        return this;
    }

    @Override
    public <X> EnhancedTypedQuery<T> setParameter(Parameter<X> arg0,
            X arg1) {
        typedQuery.setParameter(arg0, arg1);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(String arg0, Object arg1) {
        typedQuery.setParameter(arg0, arg1);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(int arg0, Object arg1) {
        typedQuery.setParameter(arg0, arg1);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            Parameter<Calendar> arg0, Calendar arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            Parameter<Date> arg0, Date arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            String arg0, Calendar arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            String arg0, Date arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            int arg0, Calendar arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public EnhancedTypedQuery<T> setParameter(
            int arg0, Date arg1, TemporalType arg2) {
        typedQuery.setParameter(arg0, arg1, arg2);
        return this;
    }

    @Override
    public int executeUpdate() {
        return typedQuery.executeUpdate();
    }

    @Override
    public int getMaxResults() {
        return typedQuery.getMaxResults();
    }

    @Override
    public int getFirstResult() {
        return typedQuery.getFirstResult();
    }

    @Override
    public Map<String, Object> getHints() {
        return typedQuery.getHints();
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return typedQuery.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return typedQuery.getParameter(name);
    }

    @Override
    public <X> Parameter<X> getParameter(String name, Class<X> type) {
        return typedQuery.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return typedQuery.getParameter(position);
    }

    @Override
    public <X> Parameter<X> getParameter(int position, Class<X> type) {
        return typedQuery.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return typedQuery.isBound(param);
    }

    @Override
    public <X> X getParameterValue(Parameter<X> param) {
        return typedQuery.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return typedQuery.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return typedQuery.getParameterValue(position);
    }

    @Override
    public FlushModeType getFlushMode() {
        return typedQuery.getFlushMode();
    }

    @Override
    public LockModeType getLockMode() {
        return typedQuery.getLockMode();
    }

    @Override
    public <X> X unwrap(Class<X> cls) {
        return unwrap(cls);
    }

}
