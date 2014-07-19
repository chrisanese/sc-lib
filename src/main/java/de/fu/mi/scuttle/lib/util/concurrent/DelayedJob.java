package de.fu.mi.scuttle.lib.util.concurrent;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * A delayed job is a wrapper around a {@link Job} which can be inserted into a
 * {@link DelayQueue}.
 * 
 * @author Julian Fleischer
 * @since 2013-11-07
 */
public class DelayedJob implements Delayed {

    private final Job job;

    private final long runAt;
    private final long repeat;

    DelayedJob(
            final Job runnable,
            final long runAt) {
        this.job = runnable;
        this.runAt = runAt;
        this.repeat = 0;
    }

    DelayedJob(
            final Job runnable,
            final long runAt,
            final long interval) {
        this.job = runnable;
        this.runAt = runAt;
        this.repeat = interval;
    }

    void execute() throws Exception {
        getJob().execute();
    }

    @Override
    public int compareTo(final Delayed o) {
        return Long.compare(
                getDelay(TimeUnit.NANOSECONDS),
                o.getDelay(TimeUnit.NANOSECONDS));
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        final long delay = runAt - System.nanoTime();
        return unit.convert(delay, TimeUnit.NANOSECONDS);
    }

    void cancel() {
        getJob().onCancel();
    }

    public long repeat() {
        return repeat;
    }

    public Job getJob() {
        return job;
    }
}
