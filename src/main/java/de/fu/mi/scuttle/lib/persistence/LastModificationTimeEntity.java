package de.fu.mi.scuttle.lib.persistence;

public interface LastModificationTimeEntity<T extends LastModificationTimeEntity<T>> {

    long getLastModificationTime();

    T setLastModificationTime(long time);
}
