package de.fu.mi.scuttle.lib.util.test;

import de.fu.mi.scuttle.lib.web.ConversionException;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleSession;

public class ScuttleRequestMockup implements ScuttleRequest {

    @Override
    public boolean isNull(String name) {
        return true;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public String get(String name) {
        return null;
    }

    @Override
    public <T> T get(String name, T defaultValue) {
        return null;
    }

    @Override
    public <T> T get(String name, Class<T> clazz) throws ConversionException {
        return null;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public boolean acceptsGzip() {
        return false;
    }

    @Override
    public ScuttleSession getSession() {
        return new ScuttleSessionMockup();
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

}
