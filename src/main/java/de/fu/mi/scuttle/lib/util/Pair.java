package de.fu.mi.scuttle.lib.util;

import java.util.Map.Entry;

public class Pair<A, B> implements Entry<A, B> {

    private final A fst;
    private final B snd;

    public Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public final A fst() {
        return fst;
    }

    public final B snd() {
        return snd;
    }

    @Override
    public A getKey() {
        return fst;
    }

    @Override
    public B getValue() {
        return snd;
    }

    @Override
    public B setValue(B value) {
        throw new UnsupportedOperationException("Pairs are immutable.");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fst == null) ? 0 : fst.hashCode());
        result = prime * result + ((snd == null) ? 0 : snd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (fst == null) {
            if (other.fst != null)
                return false;
        } else if (!fst.equals(other.fst))
            return false;
        if (snd == null) {
            if (other.snd != null)
                return false;
        } else if (!snd.equals(other.snd))
            return false;
        return true;
    }
}
