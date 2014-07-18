package de.fu.mi.scuttle.lib.web;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;

import de.fu.mi.scuttle.lib.util.JsonObject;

/**
 * An object carrying a JSON response.
 * 
 * @author Julian Fleischer
 * @since 2013-08-10
 */
public class JSONResponse implements ScuttleResponse {

    private final JSONObject json;

    private final boolean gzip;

    private JSONResponse(final boolean gzip, final JSONObject json) {
        this.json = json;
        this.gzip = gzip;
    }

    public JSONResponse(final JSONObject object) {
        this(false, object);
    }

    public JSONResponse(final JsonObject object) {
        this(false, object);
    }

    public JSONResponse(final JSONObject object, final boolean gzip) {
        this(gzip, object);
    }

    public JSONResponse(final JsonObject object, final boolean gzip) {
        this(gzip, object);
    }

    public JSONResponse() {
        this(new JSONObject(), false);
    }

    public JSONResponse(final Map<String, ? extends Object> values)
            throws JSONException {
        final JSONObject json = new JSONObject();
        for (final Entry<String, ? extends Object> entry : values.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        this.json = json;
        this.gzip = false;
    }

    @Override
    public void doResponse(
            final boolean gzipSupported,
            final ScuttleServletResponse resp) throws Exception {
        final boolean gzip = this.gzip && gzipSupported;
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        final Writer w = gzip ?
                new OutputStreamWriter(
                        new GZIPOutputStream(resp.getOutputStream()),
                        Charsets.UTF_8) : resp.getWriter();
        if (gzip) {
            resp.setHeader("Content-Encoding", "gzip");
        }
        try {
            getJsonObject().write(w);
        } finally {
            w.close();
        }
    }

    public JSONObject getJsonObject() {
        return json;
    }

}