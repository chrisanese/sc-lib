package de.fu.mi.scuttle.lib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fu.mi.scuttle.lib.web.ConversionException;
import de.fu.mi.scuttle.lib.web.Converter;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleSession;

/**
 * The actual request object.
 * 
 * @author Julian Fleischer
 * @since 2013-08-15
 */
class ScuttleRequestImpl implements ScuttleRequest {

    private final HttpServletRequest req;
    private final String path;
    private final ScuttleSessionImpl session;

    private final Map<Class<?>, Converter<?>> converters = new HashMap<>();

    {
        converters.put(Long.class, new Converter<Long>() {
            @Override
            public Long convert(String value) throws ConversionException {
                try {
                    return Long.valueOf(value);
                } catch (Exception exc) {
                    throw new ConversionException(exc);
                }
            }
        });

        converters.put(Integer.class, new Converter<Integer>() {
            @Override
            public Integer convert(String value) throws ConversionException {
                try {
                    return Integer.valueOf(value);
                } catch (Exception exc) {
                    throw new ConversionException(exc);
                }
            }
        });

        converters.put(JSONObject.class, new Converter<JSONObject>() {
            @Override
            public JSONObject convert(String value) throws ConversionException {
                try {
                    return new JSONObject(value);
                } catch (JSONException e) {
                    throw new ConversionException(e);
                }
            }
        });

        converters.put(JSONArray.class, new Converter<JSONArray>() {
            @Override
            public JSONArray convert(String value) throws ConversionException {
                try {
                    return new JSONArray(value);
                } catch (JSONException e) {
                    throw new ConversionException(e);
                }
            }
        });

    }

    protected ScuttleRequestImpl(HttpServletRequest req, String path) {
        this.req = req;
        this.path = path;
        this.session = new ScuttleSessionImpl(req.getSession());
    }

    public boolean isNull(String name) {
        return req.getParameter(name) == null;
    }

    public String get(String name) {
        return req.getParameter(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        try {
            if (!converters.containsKey(defaultValue.getClass())) {
                throw new ConversionException("No converter for "
                        + defaultValue.getClass().toString());
            }
            return (T) converters.get(defaultValue.getClass()).convert(value);
        } catch (ConversionException exc) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> clazz) throws ConversionException {
        if (!converters.containsKey(clazz)) {
            throw new ConversionException("No converter for "
                    + clazz.toString());
        }
        return (T) converters.get(clazz).convert(req.getParameter(name));
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public ScuttleSession getSession() {
        return session;
    }

    @Override
    public String getHeader(String name) {
        return req.getHeader(name);
    }

    @Override
    public boolean acceptsGzip() {
        return String.valueOf(getHeader("Accept-Encoding")).indexOf("gzip") >= 0;
    }

    @Override
    public RequestMethod getRequestMethod() {
        switch (req.getMethod()) {
        case "POST":
            return RequestMethod.POST;
        case "GET":
            return RequestMethod.GET;
        case "PUT":
            return RequestMethod.PUT;
        }
        return RequestMethod.UNKNOWN;
    }

}
