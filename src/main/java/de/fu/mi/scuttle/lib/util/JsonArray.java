package de.fu.mi.scuttle.lib.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;

public class JsonArray extends JSONArray
        implements JsonThing, Iterable<Object> {

    public JsonArray(final Collection<String> table) {
        for (final String row : table) {
            put(row);
        }
    }

    public JsonArray(final List<String> table) {
        super(table);
    }

    public JsonArray() {

    }

    public int size() {
        return super.length();
    }

    public boolean isEmpty() {
        return super.length() == 0;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Object next() {
                try {
                    return get(i++);
                } catch (final Exception exc) {
                    throw new RuntimeException(exc);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
}
