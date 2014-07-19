package de.fu.mi.scuttle.lib.web;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.json.JSONException;
import org.w3c.dom.Document;

import de.fu.mi.scuttle.lib.util.JSON;

/**
 * A PDF response, generated from an XSL-FO document.
 * 
 * This class makes use of the de-facto standard header
 * <code>Content-Disposition</code>. It is widely implemented but not defined in
 * the HTTP standard. It is however summarized in <a
 * href="http://tools.ietf.org/html/rfc6266#section-4.3">RFC 6266</a>. Further
 * explanation is available on <a href=
 * "http://stackoverflow.com/questions/1012437/uses-of-content-disposition-in-an-http-response-header"
 * >stackoverflow</a>.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public class PDFResponse implements ScuttleResponse {

    private static final ThreadLocal<FopFactory> fopFactory = new ThreadLocal<FopFactory>() {
        @Override
        protected FopFactory initialValue() {
            return FopFactory.newInstance();
        }
    };

    private final String filename;
    private final Source source;

    private final TransformerFactory factory = TransformerFactory.newInstance();
    private final Transformer transformer;

    /**
     * Creates a PDF Response that is generated from a {@link JSONResponse}.
     * 
     * @param response
     *            The JSON Response.
     * @param stylesheet
     *            The stylesheet which is used to transform the XML
     *            representation of the json response into an XSL-FO.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     * @throws ParserConfigurationException
     *             If the parser used to create the intermediate XML DOM
     *             encounters an error.
     * @throws JSONException
     *             If the JSON was errorneous in any way.
     */
    public PDFResponse(
            final JSONResponse response,
            final InputStream stylesheet)
            throws TransformerConfigurationException,
            ParserConfigurationException, JSONException {

        final Document xmlDom = JSON.toDocument(response.getJsonObject());

        this.filename = null;
        this.source = new DOMSource(xmlDom);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated from a {@link JSONResponse}.
     * 
     * @param response
     *            The JSON Response.
     * @param stylesheet
     *            The stylesheet which is used to transform the XML
     *            representation of the json response into an XSL-FO.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     * @throws ParserConfigurationException
     *             If the parser used to create the intermediate XML DOM
     *             encounters an error.
     * @throws JSONException
     *             If the JSON was errorneous in any way.
     */
    public PDFResponse(
            final JSONResponse response,
            final File stylesheet)
            throws TransformerConfigurationException,
            ParserConfigurationException, JSONException {

        final Document xmlDom = JSON.toDocument(response.getJsonObject());

        this.filename = null;
        this.source = new DOMSource(xmlDom);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final InputStream source,
            final File stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new StreamSource(source);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final Document source,
            final File stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new DOMSource(source);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final InputStream source,
            final Document stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new StreamSource(source);
        this.transformer = factory.newTransformer(new DOMSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final Document source,
            final Document stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new DOMSource(source);
        this.transformer = factory.newTransformer(new DOMSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final InputStream source,
            final InputStream stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new StreamSource(source);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated using an arbitrary XML document
     * and an XSL-T style sheet.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This may be an arbitrary XML
     *            document, i.e. it does not have to an XSL-FO document.
     * @param stylesheet
     *            The XSL-T style sheet that is used to transform the XML
     *            document into an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly (for example
     *             if the stylesheet contains an error).
     */
    public PDFResponse(
            final String filename,
            final Document source,
            final InputStream stylesheet)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new DOMSource(source);
        this.transformer = factory.newTransformer(new StreamSource(stylesheet));
    }

    /**
     * Creates a PDF Response that is generated from an XSL-FO document.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This must be an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly.
     */
    public PDFResponse(final String filename, final InputStream source)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new StreamSource(source);
        this.transformer = factory.newTransformer();
    }

    /**
     * Creates a PDF Response that is generated from an XSL-FO document.
     * 
     * @param filename
     *            The filename of the resulting PDF document.
     * @param source
     *            The source XML document. This must be an XSL-FO document.
     * @throws TransformerConfigurationException
     *             If the transformer could not be set up properly.
     */
    public PDFResponse(final String filename, final Document source)
            throws TransformerConfigurationException {
        this.filename = filename;
        this.source = new DOMSource(source);
        this.transformer = factory.newTransformer();
    }

    @Override
    public void doResponse(
            final boolean gzipSupported,
            final ScuttleServletResponse resp)
            throws Exception {
        resp.setContentType("application/pdf");
        if (filename != null) {
            resp.setHeader("Content-Disposition",
                    String.format("attachment; filename=%s", filename));
        }

        try (final OutputStream out = resp.getOutputStream()) {

            final Fop fop = fopFactory.get()
                    .newFop(MimeConstants.MIME_PDF, out);

            final Result result = new SAXResult(fop.getDefaultHandler());

            transformer.transform(source, result);
        }
    }
}
