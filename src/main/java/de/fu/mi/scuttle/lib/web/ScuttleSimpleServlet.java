package de.fu.mi.scuttle.lib.web;

import org.json.JSONObject;

import de.fu.mi.scuttle.lib.ScuttleMeta;

/**
 * A simple servlet that merely provides configuration services.
 * 
 * @author Julian Fleischer
 */
public interface ScuttleSimpleServlet {

    JSONObject getConfig();

    String getRealPath(String path);

    String getConfigString(String key, String defaultValue);

    String getConfigString(String key);

	ScuttleMeta getMeta();
}
