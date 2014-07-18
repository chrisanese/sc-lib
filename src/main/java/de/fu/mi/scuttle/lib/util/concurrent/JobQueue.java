package de.fu.mi.scuttle.lib.util.concurrent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Performs scheduled tasks in the future.
 * 
 * The job queue comprises a worker thread which jobs can be offloaded to. Jobs
 * can run immediately or after a given delay or at a certain point in time. It
 * is also possible to schedule repeating jobs.
 * 
 * @author Julian Fleischer
 * @since 2011-11-07
 */
public class JobQueue {

    private final Thread thread;

    final DelayQueue<DelayedJob> deque = new DelayQueue<>();

    final Semaphore pause = new Semaphore(1);

    final CountDownLatch stop = new CountDownLatch(1);

    boolean started = false;

    boolean stopped = false;

    /**
     * Creates a new Job Queue.
     */
    public JobQueue() {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    if (stopped && deque.isEmpty()) {
                        break;
                    }
                    DelayedJob job;
                    try {
                        pause.acquire();
                        pause.release();
                        job = deque.take();
                    } catch (final InterruptedException exc) {
                        break;
                    }
                    try {
                        job.execute();
                    } catch (final Exception exc) {
                        // TODO: Maybe use a logger
                        exc.printStackTrace(System.err);
                    } finally {
                        final long repeat = job.repeat();
                        if (repeat > 0 && !stopped) {
                            repeat(job.getJob(), repeat, TimeUnit.NANOSECONDS);
                        }
                    }
                }
                started = false;
                stop.countDown();
            }
        });
    }

    /**
     * Pauses this job queue. Note that the job queue does not immediately pause
     * the currently running thread but finishes the current job (if there is a
     * current job) first.
     * 
     * @return true iff the job queue was started, not stopped, and not paused
     *         at the moment of invocation.
     */
    public synchronized boolean pause() {
        if (started && !stopped) {
            return pause.tryAcquire();
        }
        return false;
    }

    /**
     * Unpauses this job queue.
     * 
     * @return true iff the queue was started, not stopped, and paused at the
     *         moment of invocation.
     */
    public synchronized boolean proceed() {
        if (started && !stopped && pause.availablePermits() == 0) {
            pause.release();
            return true;
        }
        return false;
    }

    /**
     * Stops this job queue. Once a job queue has been stopped it can not be
     * started again. A job queue can be stopped regardless of whether it is
     * paused or not. If you do not want the job queue to stop permanently and
     * only want to pause it use {@link #pause()}. Any jobs waiting for
     * execution will be cancelled.
     * 
     * @return true iff the queue was started and not stopped at the moment of
     *         invocation.
     */
    public synchronized boolean stop() {
        if (started && !stopped) {
            stopped = true;
            thread.interrupt();
            clear();
            return true;
        }
        return false;
    }

    /**
     * Like {@link #stop()}, but the jobs {@link Job#onCancel()} methods will
     * not be invoked.
     * 
     * @return true iff the queue was started and not stopped at the moment of
     *         invocation.
     */
    public synchronized boolean stopSilently() {
        if (started && !stopped) {
            stopped = true;
            thread.interrupt();
            deque.clear();
            return true;
        }
        return false;
    }

    /**
     * Stopps this job queue but waits for all scheduled jobs to finish. The job
     * queue will just not accept any further job submissions. After the last
     * job has been processed, this method will return.
     * 
     * @throws InterruptedException
     *             If the invoking thread is interrupted while waiting.
     */
    public void waitStop() throws InterruptedException {
        if (Thread.currentThread() == thread) {
            throw new RuntimeException(
                    "waitStop() may not be invoked from job queue (would lead to deadlock).");
        }
        synchronized (this) {
            if (!started || stopped) {
                return;
            }
            stopped = true;
        }
        stop.await();
    }

    /**
     * Starts this job queue.
     * 
     * @return true iff the queue was not started and not stopped at the moment
     *         of invocation.
     */
    public synchronized boolean start() {
        if (!started && !stopped) {
            started = true;
            thread.start();
            return true;
        }
        return false;
    }

    /**
     * Determine whether the job queue has been stopped.
     * 
     * @return true iff the job queue has been stopped. A job queue can be both
     *         started and stopped at the same time, that is it has been started
     *         and stopped but it has not yet stopped executing the current job.
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Determine whether the job queue has been started.
     * 
     * @return true iff the job queue has been started. A job queue can be both
     *         started and stopped at the same time, that is it has been started
     *         and stopped but it has not yet stopped executing the current job.
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Schedules a job for immediate execution. Same as
     * <code>submit(job, System.nanoTime())</code>.
     * 
     * @param job
     *            The job.
     */
    public void submit(final Job job) {
        deque.add(new DelayedJob(job, System.nanoTime()));
    }

    /**
     * Schedules a job for execution at the given time in nano seconds.
     * 
     * @param job
     *            The job.
     * @param runAt
     *            The point in time to run this job at, in nano seconds.
     */
    public void submit(final Job job, final long runAt) {
        deque.add(new DelayedJob(job, runAt));
    }

    /**
     * Schedules a job for delayed execution.
     * 
     * @param job
     *            The job.
     * @param delay
     *            The delay from now.
     * @param unit
     *            The unit of the delay.
     */
    public void submit(final Job job, final long delay, final TimeUnit unit) {
        final long runAt = System.nanoTime()
                + TimeUnit.NANOSECONDS.convert(delay, unit);
        deque.add(new DelayedJob(job, runAt));
    }

    /**
     * Schedules a job for repeated execution. Behaves like
     * {@link #submit(Job, long, TimeUnit)}, but reschedules the job after
     * completion.
     * 
     * @param job
     *            The job.
     * @param interval
     *            The interval.
     * @param unit
     *            The unit of the interval.
     */
    public void repeat(final Job job, final long interval, final TimeUnit unit) {
        final long delayns = TimeUnit.NANOSECONDS.convert(interval, unit);
        final long runAt = System.nanoTime() + delayns;
        deque.add(new DelayedJob(job, runAt, delayns));
    }

    /**
     * Cancels a job (removes it from the queue).
     * 
     * @param job
     *            The job to cancel. Uses object identity for comparison.
     */
    public void cancel(final Job job) {
        for (final Iterator<DelayedJob> it = deque.iterator(); it.hasNext(); it
                .next()) {
            final DelayedJob nextJob = it.next();
            if (nextJob.getJob() == job) {
                it.remove();
                nextJob.cancel();
                return;
            }
        }
    }

    /**
     * Clears all jobs waiting for execution in this queue.
     */
    public synchronized void clear() {
        for (final Iterator<DelayedJob> it = deque.iterator(); it.hasNext(); it
                .next()) {
            final DelayedJob nextJob = it.next();
            it.remove();
            nextJob.cancel();
        }
    }

    /**
     * Returns a sorted list of delayed jobs waiting for execution in this
     * queue.
     * 
     * @return The delayed jobs of this job queue as a sorted, unmodifiable
     *         list.
     */
    public List<DelayedJob> toList() {
        final DelayedJob[] jobs = (DelayedJob[]) deque.toArray();
        Arrays.sort(jobs);
        return Collections.unmodifiableList(Arrays.asList(jobs));
    }

    /**
     * Returns a sorted array of delayed jobs waiting for execution in this
     * queue.
     * 
     * @return The delayed jobs of this job queue as a sorted array.
     */
    public DelayedJob[] toArray() {
        final DelayedJob[] jobs = (DelayedJob[]) deque.toArray();
        Arrays.sort(jobs);
        return jobs;
    }
}
