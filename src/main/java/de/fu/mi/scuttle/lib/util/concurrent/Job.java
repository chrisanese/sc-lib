package de.fu.mi.scuttle.lib.util.concurrent;

/**
 * A Job is like a {@link Runnable}, but it allows for cancellation and
 * {@link #execute()} may throw an exception.
 * 
 * @author Julian Fleischer
 */
public interface Job {

    /**
     * Execute this job.
     * 
     * @throws Exception
     *             Anything may happen.
     */
    void execute() throws Exception;

    /**
     * Invoked by the {@link JobQueue} when this job is being cancelled.
     */
    void onCancel();

    /**
     * Get the comment on this job - a short description explaining what it
     * does.
     * 
     * @return The comment on this job.
     */
    String getComment();
}
