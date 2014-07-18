package de.fu.mi.scuttle.lib.web;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.fu.mi.scuttle.lib.ScuttleModule;

/**
 * Caches responses.
 * 
 * @author Julian Fleischer
 * 
 */
public class ResponseCache {

    static class CacheEntry {

        final HttpCachingResponse response;

        public CacheEntry(final HttpCachingResponse response) {
            this.response = response;
        }

        public HttpCachingResponse get() {
            return response;
        }
    }

    private final Map<ScuttleModule, ConcurrentMap<Long, CacheEntry>> store;

    public ResponseCache(final Collection<ScuttleModule> values) {
        final IdentityHashMap<ScuttleModule, ConcurrentMap<Long, CacheEntry>> store = new IdentityHashMap<>(
                values.size());

        for (final ScuttleModule handler : values) {
            store.put(handler,
                    new ConcurrentHashMap<Long, CacheEntry>());
        }

        this.store = Collections.unmodifiableMap(store);
    }

    public HttpCachingResponse get(final ScuttleModule handler,
            final long cacheTag) {
        final ConcurrentMap<Long, CacheEntry> map = store.get(handler);
        if (map == null) {
            return null;
        }
        final CacheEntry entry = map.get(cacheTag);
        if (entry == null) {
            return null;
        }
        return entry.get();
    }

    public void populate(final ScuttleModule handler, final long cacheTag,
            final HttpCachingResponse response) {
        final ConcurrentMap<Long, CacheEntry> map = store.get(handler);
        if (map != null) {
            map.put(cacheTag, new CacheEntry(response));
        }
    }

    public void evictAll() {
        for (final ConcurrentMap<Long, CacheEntry> map : store
                .values()) {
            map.clear();
        }
    }

    public void evict(final ScuttleModule handler) {
        final ConcurrentMap<Long, CacheEntry> map = store
                .get(handler);
        if (map != null) {
            map.clear();
        }
    }
}
