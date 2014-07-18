package de.fu.mi.scuttle.lib.web;

import de.fu.mi.scuttle.lib.ScuttleModule;
import de.fu.mi.scuttle.lib.persistence.DbProvider;
import de.fu.mi.scuttle.lib.persistence.EntityManager;

public abstract class AbstractScuttleModule<T extends DbProvider> implements
        ScuttleModule {

    private final T parent;

    public AbstractScuttleModule(final T parent) {
        this.parent = parent;
    }

    @Override
    public void done() throws Exception {
        // do nothing
    }

    @Override
    public void loaded() throws Exception {
        // do nothing
    }

    public T parent() {
        return parent;
    }

    public EntityManager db() {
        return parent.db();
    }

    @Override
    abstract public ScuttleResponse handle(ScuttleRequest request)
            throws Exception;

    @Override
    public long cacheTag(final ScuttleRequest req) {
        return 0;
    }
}
