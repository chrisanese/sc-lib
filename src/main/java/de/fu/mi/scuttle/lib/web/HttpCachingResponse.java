package de.fu.mi.scuttle.lib.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.io.ByteStreams;

public class HttpCachingResponse implements ScuttleServletResponse,
        ScuttleResponse {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Writer writer;
    private String characterEncoding = null;
    private Map<String, String> headers = new TreeMap<>();
    private String contentType = null;
    private byte[] bytes = null;

    public void done() {
        if (bytes == null) {
            bytes = out.toByteArray();
            out = null;
        }
    }

    @Override
    public ScuttleServletResponse setCharacterEncoding(final String string) {
        characterEncoding = string;
        return this;
    }

    @Override
    public Writer getWriter() throws IOException {
        if (writer == null) {
            writer = new OutputStreamWriter(out,
                    characterEncoding == null ? "UTF-8" : characterEncoding);
        }
        return writer;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public ScuttleServletResponse setHeader(final String key, final String value) {
        headers.put(key, value);
        return this;
    }

    @Override
    public ScuttleServletResponse setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void doResponse(final boolean gzipSupported,
            final ScuttleServletResponse response) throws IOException {
        if (bytes == null) {
            throw new RuntimeException(
                    "Empty cached response - are you sure you invoked done() after the initial response was done?");
        }
        response.setContentType(contentType);
        if (characterEncoding != null) {
            response.setCharacterEncoding(characterEncoding);
        }
        if (contentType != null) {
            response.setContentType(contentType);
        }
        for (final Entry<String, String> header : headers.entrySet()) {
            response.setHeader(header.getKey(), header.getValue());
        }
        final InputStream in = new ByteArrayInputStream(bytes);
        ByteStreams.copy(in, response.getOutputStream());
    }
}
