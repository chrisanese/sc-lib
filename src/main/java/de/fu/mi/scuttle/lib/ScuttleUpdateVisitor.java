package de.fu.mi.scuttle.lib;

public abstract class ScuttleUpdateVisitor<H extends ScuttleModule> {

    private final Class<H> handlerClass;

    public ScuttleUpdateVisitor(Class<H> handlerClass) {
        this.handlerClass = handlerClass;
    }

    @SuppressWarnings("unchecked")
    public final void doUpdate(ScuttleModule handler) {
        if (handlerClass.isAssignableFrom(handler.getClass())) {
            update((H) handler);
        }
    }

    public abstract void update(H h);
}
