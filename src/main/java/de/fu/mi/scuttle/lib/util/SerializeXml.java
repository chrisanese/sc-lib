package de.fu.mi.scuttle.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Utilities for serializing DOM objects to strings or streams. Further
 * information on <a
 * href="http://stackoverflow.com/questions/5456680/xml-document-to-string"
 * >stackoverflow</a>.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public class SerializeXml {

    private final static ThreadLocal<TransformerFactory> transformerFactory = new ThreadLocal<TransformerFactory>() {
        @Override
        protected TransformerFactory initialValue() {
            return TransformerFactory.newInstance();
        }
    };

    public static String domDocumentToString(final Document doc,
            final boolean xmlDeclaration)
            throws TransformerException {
        final Transformer transformer = transformerFactory.get()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                xmlDeclaration ? "no" : "yes");
        final StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("\r", "");
    }

    public static byte[] domDocumentToByteArray(final Document doc,
            final boolean xmlDeclaration)
            throws TransformerException {
        final Transformer transformer = transformerFactory.get()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                xmlDeclaration ? "no" : "yes");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        return out.toByteArray();
    }

    public static void domDocumentToStream(final Document doc,
            final OutputStream out,
            final boolean xmlDeclaration)
            throws TransformerException {
        final Transformer transformer = transformerFactory.get()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                xmlDeclaration ? "no" : "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }
}
