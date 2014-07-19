package de.fu.mi.scuttle.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Utilities for working with XML and the DOM a little bit more conveniently.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public final class XML {

    private XML() {
    }

    final static ThreadLocal<DocumentBuilderFactory> documentBuilderFactory = new ThreadLocal<DocumentBuilderFactory>() {
        @Override
        protected DocumentBuilderFactory initialValue() {
            return DocumentBuilderFactory.newInstance();
        }
    };

    private final static ThreadLocal<DocumentBuilder> documentBuilder = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                return documentBuilderFactory.get().newDocumentBuilder();
            } catch (final ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Creates a new DOM Document.
     * 
     * @return The newly created DOM Document.
     */
    public static Document newDocument() {
        return documentBuilder.get().newDocument();
    }

    /**
     * Creates a new DOM Document, populated with a documentElement with the
     * given tag name.
     * 
     * @param documentElementTagName
     *            The tag name from the document element.
     * @return A DOM Document that contains exactly one element node.
     */
    public static Document newDocument(final String documentElementTagName) {
        final Document doc = documentBuilder.get().newDocument();
        final Element e = doc.createElement(documentElementTagName);
        doc.appendChild(e);
        return doc;
    }

    /**
     * Creates a new DOM Document, populated with a documentElement with the
     * given tag name in the given namespace.
     * 
     * @param namespaceURI
     *            The namespace of the document element.
     * @param documentElementTagName
     *            The tag name from the document element.
     * @return A DOM Document that contains exactly one element node.
     */
    public static Document newDocument(
            final String namespaceURI,
            final String documentElementTagName) {
        final Document doc = documentBuilder.get().newDocument();
        final Element e = doc.createElementNS(
                namespaceURI, documentElementTagName);
        doc.appendChild(e);
        return doc;
    }

    public static Document parse(final File file)
            throws SAXException, IOException {
        return documentBuilder.get().parse(file);
    }

    public static Document parse(final InputStream in)
            throws SAXException, IOException {
        return documentBuilder.get().parse(in);
    }

    /**
     * Serialize a DOM Document to an arbitrary OutputStream.
     * 
     * @param doc
     *            The document.
     * @param out
     *            The output stream.
     * @throws TransformerException
     *             If an error occurred while serializing.
     */
    public static void toStream(final Document doc, final OutputStream out)
            throws TransformerException {
        SerializeXml.domDocumentToStream(doc, out, false);
    }

    public static String toString(final Document doc)
            throws TransformerException {
        return SerializeXml.domDocumentToString(doc, false);
    }

    public static byte[] toByteArray(final Document doc)
            throws TransformerException {
        return SerializeXml.domDocumentToByteArray(doc, false);
    }
}
