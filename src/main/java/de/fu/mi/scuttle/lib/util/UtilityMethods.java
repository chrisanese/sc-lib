package de.fu.mi.scuttle.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Function;
import com.google.common.io.ByteStreams;

/**
 * Utility methods for basic tasks, like creating a {@link Pair}, creating a
 * {@link List} or a {@link Map}, etc.
 * 
 * @author Julian Fleischer
 */
public class UtilityMethods {

    public static <A, B> Pair<A, B> pair(final A fst, final B snd) {
        return new Pair<>(fst, snd);
    }

    /**
     * Creates a list with the given elements.
     * 
     * @param elements
     *            The elements - varargs, that is you can call this method like
     *            <code>list(1, 2, 3)</code>.
     * @return The newly created list. This is currently an {@link ArrayList},
     *         but you should not rely on this information. It is however
     *         guaranteed that the resulting {@link List} is modifiable.
     */
    @SafeVarargs
    public static <E> List<E> list(final E... elements) {
        final List<E> list = new ArrayList<>(elements.length);
        for (final E element : elements) {
            list.add(element);
        }
        return list;
    }

    /**
     * Creates a map with the given key/value pairs.
     * 
     * @param elements
     *            The entries of the map, given as a varargs of {@link Pair}s.
     * @return The newly created map. This is currently a {@link HashMap}, but
     *         you should not rely on this information. It is however guaranteed
     *         that the resulting {@link Map} is modifiable.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> map(final Pair<K, V>... elements) {
        final Map<K, V> map = new HashMap<>(elements.length);
        for (final Pair<K, V> element : elements) {
            map.put(element.fst(), element.snd());
        }
        return map;
    }

    /**
     * Gzip compresses the given byte array.
     * 
     * @param bytes
     *            The byte array to compress.
     * @return The gzip compressed byte array.
     * @throws IOException
     *             If the compression fails (this should never happen, but the
     *             contracts of most OutputStreams say so.
     */
    public static byte[] gzipCompress(final byte[] bytes) throws IOException {

        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(
                bytes.length);
        final OutputStream gzip = new GZIPOutputStream(out);
        ByteStreams.copy(in, gzip);
        in.close();
        gzip.close();

        return out.toByteArray();
    }

    public static <A, B> ArrayList<B> map(final Function<A, B> f,
            final ArrayList<A> xs) {
        final ArrayList<B> g = new ArrayList<B>(xs.size());
        for (final A x : xs) {
            g.add(f.apply(x));
        }
        return g;
    }

    public static <A, B> LinkedList<B> map(final Function<A, B> f,
            final LinkedList<A> xs) {
        final LinkedList<B> g = new LinkedList<B>();
        for (final A x : xs) {
            g.add(f.apply(x));
        }
        return g;
    }

    @SuppressWarnings("unchecked")
    public static <A, B> List<B> map(final Function<A, B> f, final List<A> xs) {
        List<B> g;
        try {
            g = xs.getClass().newInstance();
        } catch (final Exception exc) {
            throw new ReflectionException(exc);
        }
        for (final A x : xs) {
            g.add(f.apply(x));
        }
        return g;
    }

    @SuppressWarnings("unchecked")
    public static <A, B> Collection<B> map(final Function<A, B> f,
            final Collection<A> xs) {
        Collection<B> g;
        try {
            g = xs.getClass().newInstance();
        } catch (final Exception exc) {
            throw new ReflectionException(exc);
        }
        for (final A x : xs) {
            g.add(f.apply(x));
        }
        return g;
    }

    public static <A, B, L extends Collection<B>> L map(
            final Function<A, B> f,
            final Collection<A> xs, final Class<L> target) {
        L g;
        try {
            g = target.newInstance();
        } catch (final Exception exc) {
            throw new ReflectionException(exc);
        }
        for (final A x : xs) {
            g.add(f.apply(x));
        }
        return g;
    }

    public static <A, B> B[] map(
            final Function<A, B> f, final A[] xs) {
        @SuppressWarnings("unchecked")
        final B[] xs2 = (B[]) new Object[xs.length];
        for (int i = 0; i < xs.length; i++) {
            xs2[i] = f.apply(xs[i]);
        }
        return xs2;
    }
}
