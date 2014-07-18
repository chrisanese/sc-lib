package de.fu.mi.scuttle.lib.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface ScuttleServletResponse {

    ScuttleServletResponse setCharacterEncoding(String string);

    Writer getWriter() throws IOException;

    OutputStream getOutputStream() throws IOException;

    ScuttleServletResponse setHeader(String key, String value);

    ScuttleServletResponse setContentType(String string);
}
