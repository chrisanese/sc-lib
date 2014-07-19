package de.fu.mi.scuttle.lib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import de.fu.mi.scuttle.lib.web.ScuttleServletResponse;

public class HttpScuttleServletResponse implements ScuttleServletResponse {

    private final HttpServletResponse httpResponse;

    public HttpScuttleServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpScuttleServletResponse setCharacterEncoding(
            String characterEncoding) {
        httpResponse.setCharacterEncoding(characterEncoding);
        return this;
    }

    @Override
    public Writer getWriter() throws IOException {
        return httpResponse.getWriter();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return httpResponse.getOutputStream();
    }

    @Override
    public HttpScuttleServletResponse setHeader(String key, String value) {
        httpResponse.setHeader(key, value);
        return this;
    }

    @Override
    public ScuttleServletResponse setContentType(String contentType) {
        httpResponse.setContentType(contentType);
        return this;
    }

}
