package de.fu.mi.scuttle.lib;

import javax.servlet.http.HttpSession;

import com.google.common.collect.Iterators;

import de.fu.mi.scuttle.lib.web.ScuttleSession;

/**
 * 
 * @author Julian Fleischer
 */
public class ScuttleSessionImpl implements ScuttleSession {

    private final HttpSession session;

    ScuttleSessionImpl(final HttpSession session) {
        this.session = session;
    }

    @Override
    public Object get(final String key) {
        try {
            return session.getAttribute(key);
        } catch (final Exception exc) {
            return null;
        }
    }

    @Override
    public String getString(final String key) {
        return String.valueOf(get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final String key, final T defaultValue) {
        final Object value = get(key);

        if (value == null) {
            return defaultValue;
        }
        if (defaultValue != null
                && !defaultValue.getClass().isAssignableFrom(value.getClass())) {
            return defaultValue;
        }
        return (T) value;
    }

    @Override
    public void put(final String key, final Object value) {
        session.setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        final String[] values = Iterators
                .toArray(Iterators.forEnumeration(session.getAttributeNames()),
                        String.class);
        for (final String name : values) {
            session.removeAttribute(name);
        }
    }

    @Override
    public void remove(final String name) {
        session.removeAttribute(name);
    }
}
