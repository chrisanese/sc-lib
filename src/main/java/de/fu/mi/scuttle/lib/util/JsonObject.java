package de.fu.mi.scuttle.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Iterables;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.*;

public class JsonObject extends JSONObject implements JsonThing,
        Map<String, Object>, Iterable<String> {

    public JsonObject() {

    }

    public JsonObject(Map<String, String> map) {
        super(map);
    }

    @Override
    public int size() {
        return super.length();
    }

    @Override
    public boolean isEmpty() {
        return super.length() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return super.has(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Object get(Object key) {
        try {
            return super.get(key.toString());
        } catch (JSONException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Object remove(Object key) {
        return super.remove(key.toString());
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (java.util.Map.Entry<? extends String, ? extends Object> e : m
                .entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        for (String key : this) {
            remove(key);
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        Iterables.addAll(keySet, this);
        return keySet;
    }

    @Override
    public Collection<Object> values() {
        ArrayList<Object> array = new ArrayList<>(size());
        for (String key : this) {
            try {
                array.add(get(key));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return array;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entrySet = new HashSet<>();

        for (String key : this) {
            try {
                entrySet.add(pair(key, get(key)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return entrySet;
    }

    @Override
    public JsonObject put(String key, Object value) {
        try {
            super.put(key, value);
            return this;
        } catch (JSONException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public JsonObject put(String arg0, boolean arg1) throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @Override
    public JsonObject put(String arg0,
            @SuppressWarnings("rawtypes") Collection arg1) throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @Override
    public JsonObject put(String arg0, double arg1) throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @Override
    public JsonObject put(String arg0, int arg1) throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @Override
    public JsonObject put(String arg0, long arg1) throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @Override
    public JsonObject put(String arg0, @SuppressWarnings("rawtypes") Map arg1)
            throws JSONException {
        super.put(arg0, arg1);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<String> iterator() {
        return keys();
    }
}
