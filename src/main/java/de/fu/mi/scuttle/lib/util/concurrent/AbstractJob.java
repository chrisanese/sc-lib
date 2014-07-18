package de.fu.mi.scuttle.lib.util.concurrent;

/**
 * Convenience class that implements {@link Job#onCancel()} so you do not have
 * to. You can however still override it.
 * 
 * @author Julian Fleischer
 * @since 2013-11-07
 */
public abstract class AbstractJob implements Job {

    private final String comment;

    public AbstractJob(final String comment) {
        this.comment = comment;
    }

    @Override
    public void onCancel() {

    }

    @Override
    public String getComment() {
        return comment;
    }
}
