package de.fu.mi.scuttle.lib.web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Charsets;

/**
 * An object carrying a complete response of bytes.
 * 
 * @author Julian Fleischer
 * @since 2013-09-25
 */
public class PlainResponse implements ScuttleResponse {

    private final byte[] data;
    private final Map<String, String> headers = new HashMap<>();

    public PlainResponse(final byte[] bytes) {
        this.data = bytes;
    }

    public PlainResponse(final byte[] bytes, final String contentType) {
        this.data = bytes;
        setHeader("Content-Type", contentType);
        setHeader("Content-Length", String.valueOf(this.data.length));
    }

    public PlainResponse(final byte[] bytes, final Map<String, String> headers) {
        this.data = bytes;
        this.headers.putAll(headers);
        setHeader("Content-Length", String.valueOf(this.data.length));
    }

    public PlainResponse(final String string) {
        this.data = string.getBytes(Charsets.UTF_8);
        setHeader("Content-Length", String.valueOf(this.data.length));
    }

    public PlainResponse(final String string, final Charset charset) {
        this.data = string.getBytes(charset);
        setHeader("Content-Length", String.valueOf(this.data.length));
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public final void doResponse(
            final boolean _,
            final ScuttleServletResponse resp)
            throws IOException {
        for (final Entry<String, String> header : headers.entrySet()) {
            resp.setHeader(header.getKey(), header.getValue());
        }
        resp.getOutputStream().write(data);
    }
}