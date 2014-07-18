package de.fu.mi.scuttle.lib.util;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Utility methods for working with HTML, which might not be well-formed XML.
 * 
 * This class utilizes the Jsoup HTML5 parser.
 * 
 * @see <a href="http://www.jsoup.org/">www.jsoup.org</a>
 * 
 * @author Julian Fleischer
 */
public final class HTML {

    private HTML() {
    }

    public static JsonObject htmlStringToJson(final String html)
            throws JSONException {
        final Document doc = Jsoup.parse(html);

        final JsonObject json = new JsonObject();
        final JsonArray children = new JsonArray();

        for (final Node node : doc.childNodes()) {
            children.put(nodeToJson(node));
        }
        json.put("children", children);

        return null;
    }

    public static JsonObject nodeToJson(final Node html) throws JSONException {
        final JsonObject json = new JsonObject();

        if (html instanceof Element) {
            final Element e = (Element) html;

            json.put("tagName", e.tagName());

            final JsonArray children = new JsonArray();
            for (final Node node : e.childNodes()) {
                children.put(nodeToJson(node));
            }
            json.put("children", children);

            final JsonObject attributes = new JsonObject();
            for (final Attribute a : e.attributes()) {
                attributes.put(a.getKey(), a.getValue());
            }
            json.put("attributes", attributes);

        } else if (html instanceof TextNode) {
            final TextNode t = (TextNode) html;

            json.put("text", t.text());
        }

        return null;
    }
}
