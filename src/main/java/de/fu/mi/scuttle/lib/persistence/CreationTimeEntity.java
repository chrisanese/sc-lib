package de.fu.mi.scuttle.lib.persistence;

public interface CreationTimeEntity<T extends CreationTimeEntity<T>> {

    long getCreationTime();

    T setCreationTime(long time);
}
