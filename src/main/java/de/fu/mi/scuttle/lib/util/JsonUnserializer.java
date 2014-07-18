package de.fu.mi.scuttle.lib.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import de.fu.mi.scuttle.lib.persistence.UuidEntity;

public class JsonUnserializer<T> {

    final Class<T> clazz;

    /**
     * Unserializers for the specific properties.
     */
    private final Map<String, Unserializer> unserializers = new ConcurrentHashMap<>();

    public interface ObjectResolver {
        Object resolve(Class<?> type, String uuid);
    }

    public static class UnserializerInitializationException extends
            UnserializerException {

        private static final long serialVersionUID = 8399941666119088772L;

        public UnserializerInitializationException(final Throwable cause) {
            super(cause);
        }
    }

    public static class UnserializerException extends RuntimeException {

        private static final long serialVersionUID = 2871594004284114202L;

        public UnserializerException(final Throwable cause) {
            super(cause);
        }
    }

    public JsonUnserializer(final Class<T> clazz) {
        this.clazz = clazz;

        try {
            final BeanInfo info = Introspector.getBeanInfo(clazz);
            final PropertyDescriptor[] descriptors = info
                    .getPropertyDescriptors();
            for (final PropertyDescriptor property : descriptors) {
                final String name = property.getName();
                if (!"class".equals(name) && !"id".equals(name)) {
                    unserializers.put(name, new Unserializer(property));
                }
            }
        } catch (final IntrospectionException exc) {
            throw new UnserializerInitializationException(exc);
        } catch (final NoSuchMethodException exc) {
            throw new UnserializerInitializationException(exc);
        }
    }

    /**
     * Unserializer for a specific property.
     */
    class Unserializer {
        private final String name;
        private final Method write;
        private final Class<?> type;

        Unserializer(final PropertyDescriptor desc)
                throws NoSuchMethodException {
            this.name = desc.getName();
            this.type = desc.getPropertyType();

            final Method write = clazz.getMethod(
                    "set" + StringUtils.capitalize(name), type);

            this.write = write;
        }

        public void unserialize(
                final Object object,
                final Object value,
                final ObjectResolver resolver)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            if (JSONObject.NULL.equals(value)) {
                write.invoke(object, (Object) null);
            } else if (String.class.isAssignableFrom(type)) {
                write.invoke(object, String.valueOf(value));
            } else if (int.class.equals(type)) {
                write.invoke(object, ((Number) value).intValue());
            } else if (long.class.equals(type)) {
                write.invoke(object, ((Number) value).longValue());
            } else if (Integer.class.isAssignableFrom(type)) {
                write.invoke(object, ((Number) value).intValue());
            } else if (Long.class.isAssignableFrom(type)) {
                write.invoke(object, ((Number) value).longValue());
            } else if (Boolean.class.isAssignableFrom(type)) {
                write.invoke(object,
                        "true".equals(type) || Boolean.TRUE.equals(type));
            } else if (Double.class.isAssignableFrom(type)) {
                write.invoke(object, ((Number) value).doubleValue());
            } else if (UuidEntity.class.isAssignableFrom(type)) {
                write.invoke(object, resolver.resolve(type, value.toString()));
            }
        }
    }

    public T unserialize(
            final JSONObject json,
            final ObjectResolver resolver) {
        try {
            final T object = clazz.newInstance();
            for (final Entry<String, Unserializer> unserializer : unserializers
                    .entrySet()) {
                if (json.has(unserializer.getKey())) {
                    unserializer.getValue().unserialize(
                            object, json.get(unserializer.getKey()), resolver);
                }
            }
            return object;
        } catch (final Exception exc) {
            throw new UnserializerException(exc);
        }
    }

}
