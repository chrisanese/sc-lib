package de.fu.mi.scuttle.lib.persistence;

public interface UuidEntity<T extends UuidEntity<T>> {

    String getUuid();

    T setUuid(String uuid);
}
