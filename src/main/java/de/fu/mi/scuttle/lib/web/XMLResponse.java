package de.fu.mi.scuttle.lib.web;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.Document;

import de.fu.mi.scuttle.lib.util.JSON;
import de.fu.mi.scuttle.lib.util.XML;

/**
 * An XML Response.
 * 
 * @author Julian Fleischer
 */
public class XMLResponse implements ScuttleResponse {

    private final Document domDocument;

    public XMLResponse(final JSONResponse jsonResponse)
            throws ParserConfigurationException, JSONException {
        domDocument = JSON.toDocument(jsonResponse.getJsonObject());
    }

    public XMLResponse(final Document document) {
        domDocument = document;
    }

    @Override
    public void doResponse(final boolean gzipSupported,
            final ScuttleServletResponse resp)
            throws Exception {
        resp.setContentType("application/xml");
        resp.setCharacterEncoding("UTF-8");
        XML.toStream(domDocument, resp.getOutputStream());
    }
}
