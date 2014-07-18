package de.fu.mi.scuttle.lib.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import de.fu.mi.scuttle.lib.persistence.UuidEntity;

public class JsonSerializer<T> {

    /**
     * The serializers for the proproperties of the class.
     */
    private final Map<String, Serializer> serializers = new ConcurrentHashMap<>();

    /**
     * The class.
     */
    private final Class<T> clazz;

    public static class SerializerInitializationException extends
            SerializerException {

        private static final long serialVersionUID = -5337390476701875376L;

        public SerializerInitializationException(final Throwable cause) {
            super(cause);
        }
    }

    public static class SerializerException extends RuntimeException {

        private static final long serialVersionUID = 2871594004284114202L;

        public SerializerException(final Throwable cause) {
            super(cause);
        }
    }

    public JsonSerializer(final Class<T> clazz) {

        this.clazz = clazz;

        try {
            final BeanInfo info = Introspector.getBeanInfo(clazz);
            final PropertyDescriptor[] descriptors = info
                    .getPropertyDescriptors();
            for (final PropertyDescriptor property : descriptors) {
                final String name = property.getName();
                if (!"class".equals(name) && !"id".equals(name)) {
                    serializers.put(name, new Serializer(property));
                }
            }
        } catch (final IntrospectionException e) {
            throw new SerializerInitializationException(e);
        }
    }

    /**
     * Serializer for a specific property.
     */
    class Serializer {
        private final String name;
        private final Method read;

        Serializer(final PropertyDescriptor desc) {
            this.name = desc.getName();
            this.read = desc.getReadMethod();
        }

        void serialize(final Object object, final JsonObject json) {
            try {
                final Object value = read.invoke(object);
                if (value == null) {
                    json.put(this.name, JSONObject.NULL);
                } else if (value instanceof String) {
                    json.put(this.name, String.valueOf(value));
                } else if (value instanceof Integer) {
                    json.put(this.name, (int) (Integer) value);
                } else if (value instanceof Long) {
                    json.put(this.name, (long) (Long) value);
                } else if (value instanceof Boolean) {
                    json.put(this.name, (boolean) (Boolean) value);
                } else if (value instanceof Double) {
                    json.put(this.name, (double) (Double) value);
                } else if (value instanceof UuidEntity) {
                    json.put(this.name, ((UuidEntity<?>) value).getUuid());
                }
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | JSONException exc) {
                throw new SerializerException(exc);
            }
        }
    }

    public JsonArray serialize(final Collection<T> collection)
            throws JSONException {
        final JsonArray json = new JsonArray();
        for (final T object : collection) {
            json.put(serialize(object));
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    public JsonArray serializeAny(final Collection<?> collection)
            throws JSONException {
        final JsonArray json = new JsonArray();
        for (final Object object : collection) {
            if (clazz.isAssignableFrom(object.getClass())) {
                json.put(serialize((T) object));
            }
        }
        return json;
    }

    public JsonObject serialize(final T object) throws JSONException {
        final JsonObject json = new JsonObject();
        for (final Entry<String, Serializer> entry : serializers.entrySet()) {
            entry.getValue().serialize(object, json);
        }
        return json;
    }

    public void serialize(final T object, final Writer writer)
            throws JSONException {
        serialize(object).write(writer);
    }
}
