package de.fu.mi.scuttle.lib.util.test;

import de.fu.mi.scuttle.lib.web.ScuttleSession;

public class ScuttleSessionMockup implements ScuttleSession {

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        return defaultValue;
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void remove(String string) {

    }
}
