package de.fu.mi.scuttle.lib.util;

import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Utility methods for working with JSON data.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public final class JSON {

    private JSON() {
    }

    public static enum JSONType {
        STRING, OBJECT, ARRAY, NUMBER, BOOLEAN, NULL
    }

    public static JSONType STRING = JSONType.STRING;
    public static JSONType OBJECT = JSONType.OBJECT;
    public static JSONType ARRAY = JSONType.ARRAY;
    public static JSONType NUMBER = JSONType.NUMBER;
    public static JSONType BOOLEAN = JSONType.BOOLEAN;
    public static JSONType NULL = JSONType.NULL;

    public static Document toDocument(final JSONObject object)
            throws ParserConfigurationException, JSONException {
        return JsonToXml.jsonToDomDocument(object);
    }

    public static String toXML(final JSONObject object)
            throws ParserConfigurationException, JSONException,
            TransformerException {
        return XML.toString(JsonToXml.jsonToDomDocument(object));
    }

    public static String toString(final JSONObject object) throws JSONException {
        final StringWriter stringWriter = new StringWriter();
        object.write(stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
