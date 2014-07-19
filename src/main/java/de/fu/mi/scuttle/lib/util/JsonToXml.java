package de.fu.mi.scuttle.lib.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Function;

/**
 * Utilities for transforming JSON data to XML data.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public class JsonToXml {

    private final static ThreadLocal<DocumentBuilderFactory> builderFactory = new ThreadLocal<DocumentBuilderFactory>() {
        @Override
        protected DocumentBuilderFactory initialValue() {
            return DocumentBuilderFactory.newInstance();
        }
    };

    public static Document jsonToDomDocument(final JSONObject object)
            throws ParserConfigurationException, JSONException {
        final DocumentBuilder builder = builderFactory.get()
                .newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element e = doc.createElement("object");
        jsonObjectToXml(doc, e, object);
        doc.appendChild(e);

        return doc;
    }

    public static Document jsonToDomDocument(
            final JSONObject object,
            final Map<String, Function<String, Element>> map)
            throws ParserConfigurationException, JSONException {
        final DocumentBuilder builder = builderFactory.get()
                .newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element e = doc.createElement("object");
        jsonObjectToXml(doc, e, object);
        doc.appendChild(e);

        return doc;
    }

    public static Document jsonToDomDocument(final JSONArray array)
            throws ParserConfigurationException, JSONException {
        final DocumentBuilder builder = builderFactory.get()
                .newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element e = doc.createElement("array");
        jsonArrayToXml(doc, e, array);
        doc.appendChild(e);

        return doc;
    }

    @SuppressWarnings("unchecked")
    public static void jsonObjectToXml(final Document doc, final Element e,
            final JSONObject object) throws JSONException {

        for (final Iterator<String> it = object.keys(); it.hasNext();) {
            final String key = it.next();
            final Object value = object.get(key);
            final Element k = createElement(doc, value);
            k.setAttribute("key", key);
            e.appendChild(k);
        }
    }

    public static void jsonArrayToXml(final Document doc, final Element e,
            final JSONArray array) throws JSONException {

        for (int i = 0; i < array.length(); i++) {
            final Object value = array.get(i);
            final Element k = createElement(doc, value);
            e.appendChild(k);
        }
    }

    private static Element createElement(final Document doc, final Object value)
            throws JSONException {
        if (value == JSONObject.NULL) {
            return doc.createElement("null");
        } else if (value instanceof JSONObject) {
            final Element e = doc.createElement("object");
            jsonObjectToXml(doc, e, (JSONObject) value);
            return e;
        } else if (value instanceof JSONArray) {
            final Element e = doc.createElement("array");
            jsonArrayToXml(doc, e, (JSONArray) value);
            return e;
        } else if (value instanceof String) {
            final Element e = doc.createElement("string");
            e.appendChild(doc.createTextNode(sanitize((String) value)));
            return e;
        } else if (value instanceof Integer) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(value.toString()));
            return e;
        } else if (value instanceof Long) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(value.toString()));
            return e;
        } else if (value instanceof BigInteger) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(value.toString()));
            return e;
        } else if (value instanceof Float) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(value.toString()));
            return e;
        } else if (value instanceof Double) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(String.valueOf(value)));
            return e;
        } else if (value instanceof BigDecimal) {
            final Element e = doc.createElement("number");
            e.appendChild(doc.createTextNode(value.toString()));
            return e;
        } else if (value instanceof Boolean) {
            final Element e = doc.createElement("bool");
            e.appendChild(doc
                    .createTextNode((Boolean) value ? "true" : "false"));
            return e;
        } else {
            final Element e = doc.createElement("string");
            e.appendChild(doc.createTextNode(sanitize(String.valueOf(value))));
            return e;
        }
    }

    private static String sanitize(final String value) {
        return value
                .replaceAll(
                        "["
                                + "\\x00\\x01\\x02\\x03\\x04\\x05\\x06\\x07\\x08"
                                + "\\x0B\\x0D\\x0E\\x0F"
                                + "\\x10\\x11\\x12\\x13\\x14\\x15\\x16\\x17\\x18"
                                + "\\x19\\x1A\\x1B\\x1C\\x1D\\x1D\\x1E\\x1F"
                                + "]+",
                        "");
    }
}
