package de.fu.mi.scuttle.lib.web;

public interface ScuttleResponse {

    void doResponse(boolean gzipSupported, ScuttleServletResponse resp)
            throws Exception;

}
