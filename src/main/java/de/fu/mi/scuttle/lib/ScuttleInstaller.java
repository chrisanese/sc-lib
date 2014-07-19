package de.fu.mi.scuttle.lib;

import org.json.JSONObject;

import de.fu.mi.scuttle.lib.persistence.EntityManager;

public interface ScuttleInstaller {

    void install(JSONObject config, EntityManager entityManager)
            throws Exception;

}
