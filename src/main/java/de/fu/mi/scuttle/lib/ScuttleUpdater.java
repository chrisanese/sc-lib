package de.fu.mi.scuttle.lib;

import java.util.Map;

public interface ScuttleUpdater {

    void update(Map<String, ScuttleModule> handlers);

}
