package de.fu.mi.scuttle.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

/**
 * Static methods for synchronously executing a {@link ProcessBuilder}s
 * {@link Process}.
 * 
 * @author Julian Fleischer
 * @since 2013-11-04
 */
public class ProcessExecutor {

    public static class ExecutionException extends IOException {

        /**
         * 
         */
        private static final long serialVersionUID = -6967324304284064937L;

        private final Result result;

        public ExecutionException(final Result result) {
            super(String.format("ExitCode: %d\nOutStream: %s\nErrStream: %s\n",
                    result.getExitCode(),
                    result.getOutString(),
                    result.getErrString()));

            this.result = result;

        }

        public Result getResult() {
            return result;
        }
    }

    /**
     * Holds the result of a process execution: OutStream, ErrStream, ExitCode.
     * If the execution was interrupted or an IOException occurred, the Result
     * will hold that exception as well. In this case the ExitCode will be
     * <code>-1</code>.
     * 
     * @author Julian Fleischer
     */
    public static class Result {

        private final Exception exception;
        private final int exitCode;
        private final ByteArrayOutputStream out;
        private final ByteArrayOutputStream err;

        Result(final int i, final ByteArrayOutputStream out,
                final ByteArrayOutputStream err) {
            exitCode = i;

            this.out = out;
            this.err = err;

            exception = null;
        }

        public Result(final Exception e) {

            try {
                out = new ByteArrayOutputStream(0);
                out.close();
                err = new ByteArrayOutputStream(0);
                err.close();
            } catch (final IOException exc) {
                throw new RuntimeException(exc);
            }

            exitCode = -1;

            exception = e;
        }

        public int getExitCode() {
            return exitCode;
        }

        public byte[] getOutBytes() {
            return out.toByteArray();
        }

        public byte[] getErrBytes() {
            return err.toByteArray();
        }

        public String getOutString() {
            return new String(out.toByteArray(), Charsets.UTF_8);
        }

        public String getErrString() {
            return new String(err.toByteArray(), Charsets.UTF_8);
        }

        public Exception getException() {
            return exception;
        }

        public boolean wasSuccessfull() {
            return exitCode == 0;
        }

        public boolean wasException() {
            return exception != null;
        }
    }

    /**
     * Executes the command given by the <code>processBuilder</code>, see
     * {@link ProcessBuilder}. Results of executing the external command are
     * wrapped in a {@link Result} object.
     * 
     * @param processBuilder
     *            The processBuilder that will be used to create the process.
     * @return ExitCode, Err-Stream, and Out-Stream are returned as a Result.
     */
    public static Result executeProcess(final ProcessBuilder processBuilder) {

        try {

            final Process p = processBuilder.start();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ByteArrayOutputStream err = new ByteArrayOutputStream();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ByteStreams.copy(p.getInputStream(), out);
                    } catch (final IOException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ByteStreams.copy(p.getErrorStream(), err);
                    } catch (final IOException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }).start();

            p.waitFor();

            return new Result(p.exitValue(), out, err);
        } catch (final Exception e) {
            return new Result(e);
        }
    }
}
