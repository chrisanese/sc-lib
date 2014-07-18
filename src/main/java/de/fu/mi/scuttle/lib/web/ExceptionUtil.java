package de.fu.mi.scuttle.lib.web;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fu.mi.scuttle.lib.ScuttleLoginException;
import de.fu.mi.scuttle.lib.ScuttleNoPermissionException;

/**
 * Static methods for reporting Exceptions.
 * 
 * @author Julian Fleischer
 * @since 2013-10-21
 */
public class ExceptionUtil {

    private static void handleException(
            final JSONObject result,
            final ConstraintViolationException exc) throws JSONException {
        final JSONArray violations = new JSONArray();
        for (final ConstraintViolation<?> v : exc.getConstraintViolations()) {
            final JSONObject jv = new JSONObject();
            jv.put("value", v.getInvalidValue());
            jv.put("what", v.getPropertyPath().toString());
            jv.put("message", v.getMessage());
            violations.put(jv);
        }
        result.put("violations", violations);
    }

    private static void handleException(
            final JSONObject result,
            final RollbackException exc)
            throws JSONException {
        if (exc.getCause() instanceof ConstraintViolationException) {
            handleException(result,
                    (ConstraintViolationException) exc.getCause());
        }
    }

    private static JSONObject serializeStackTraceElement(
            final StackTraceElement t)
            throws JSONException {
        final JSONObject obj = new JSONObject();

        obj.put("class", t.getClassName());
        obj.put("method", t.getMethodName());
        obj.put("file", t.getFileName());
        obj.put("line", t.getLineNumber());

        return obj;
    }

    private static JSONObject serializeThrowable(final Throwable e)
            throws JSONException {
        final JSONObject obj = new JSONObject();

        obj.put("message", e.getMessage());
        obj.put("type", e.getClass().getName());

        final JSONArray trace = new JSONArray();
        for (final StackTraceElement t : e.getStackTrace()) {
            trace.put(serializeStackTraceElement(t));
        }
        obj.put("trace", trace);

        return obj;
    }

    private static void handleException(
            final JSONObject result,
            final Exception exc)
            throws JSONException {
        final JSONObject obj = serializeThrowable(exc);
        JSONObject parent = obj;
        Throwable e = exc;
        while ((e = e.getCause()) != null) {
            final JSONObject o = serializeThrowable(e);
            parent.put("causedBy", o);
            parent.put("hasCause", true);
            parent = o;
        }
        parent.put("hasCause", false);
        result.put("exception", obj);
    }

    public static JSONResponse handleException(final Exception exc) {
        final JSONObject result = new JSONObject();
        try {
            result.put("success", false);
            if (exc instanceof ConstraintViolationException) {
                handleException(result, (ConstraintViolationException) exc);
            } else if (exc instanceof RollbackException) {
                handleException(result, (RollbackException) exc);
            } else {
                handleException(result, exc);
            }
        } catch (final JSONException e) {
            e.printStackTrace(System.err);
        }
        return new JSONResponse(result);
    }

    public static void printExceptions(
            final Writer writer,
            final List<? extends Throwable> exc) throws IOException {

        for (Throwable e : exc) {
            do {
                writer.write(e.getClass().getName());
                writer.write(": ");
                writer.write(String.valueOf(e.getMessage()));
                writer.write("\n");

                final StackTraceElement[] trace = e.getStackTrace();
                writer.write("  in ");
                writer.write(String.valueOf(trace[0].getClassName()));
                writer.write(":");
                writer.write(String.valueOf(trace[0].getLineNumber()));
                writer.write("\n");
            } while ((e = e.getCause()) != null);
            writer.write("\n");
        }
    }

    public static void error500(
            final HttpServletResponse resp,
            final List<Exception> exc) throws IOException {
        resp.setContentType("text/plain");
        try (Writer writer = resp.getWriter()) {
            printExceptions(writer, exc);
        }
    }

    public static void error403(
            final HttpServletResponse resp,
            final ScuttleNoPermissionException exc) throws IOException {
        resp.setContentType("text/plain");
        resp.setStatus(403);
        try (Writer writer = resp.getWriter()) {
            writer.write("403 Forbidden");
        }
    }

    public static void error404(
            final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setStatus(404);
        try (Writer writer = resp.getWriter()) {
            writer.write("404 Not Found");
        }
    }

    public static void loginError(
            final HttpServletResponse resp,
            final ScuttleLoginException exc) throws IOException, JSONException {
        resp.setContentType("application/json");
        final JSONObject message = new JSONObject();

        message.put("loginError", exc.getMessage());
        message.put("success", false);

        try (Writer writer = resp.getWriter()) {
            message.write(writer);
        }
    }
}
