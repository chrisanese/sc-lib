package de.fu.mi.scuttle.lib.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utilities for transforming XML data to JSON data.
 * 
 * @author Julian Fleischer
 * @since 2013-11-09
 */
public class XmlToJson {

    public static JsonThing domDocumentToJson(final Document doc) {

        final Element e = doc.getDocumentElement();

        switch (e.getTagName()) {
        case "object":
            return elementToJsonObject(e);
        case "array":
            return elementToJsonArray(e);
        }

        return null;
    }

    private static JsonThing elementToJsonArray(final Element e) {
        final NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {

        }
        return null;
    }

    private static JsonThing elementToJsonObject(final Element e) {
        final NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {

        }
        return null;
    }
}
