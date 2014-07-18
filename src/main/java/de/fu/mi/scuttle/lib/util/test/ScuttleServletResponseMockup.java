package de.fu.mi.scuttle.lib.util.test;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.codec.Charsets;

import de.fu.mi.scuttle.lib.web.ScuttleServletResponse;

public class ScuttleServletResponseMockup implements ScuttleServletResponse,
        Closeable {

    private final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
    private final OutputStreamWriter writer = new OutputStreamWriter(
            byteArrayOut, Charsets.UTF_8);

    @Override
    public ScuttleServletResponse setCharacterEncoding(String characterEncoding) {
        return this;
    }

    @Override
    public Writer getWriter() throws IOException {
        return writer;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return byteArrayOut;
    }

    @Override
    public ScuttleServletResponse setHeader(String key, String value) {
        return this;
    }

    @Override
    public ScuttleServletResponse setContentType(String string) {
        return this;
    }

    public String toString() {
        return new String(byteArrayOut.toByteArray(), Charsets.UTF_8);
    }

    public byte[] toByteArray() {
        return byteArrayOut.toByteArray();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
