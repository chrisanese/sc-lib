package de.fu.mi.scuttle.lib;

import java.util.List;

import de.fu.mi.scuttle.lib.persistence.EntityManager;

/**
 * 
 * @author Julian Fleischer
 */
public interface ScuttleMeta {

	String getReleaseDate();

	String getVersion();

	String getFirstPage();
	
	boolean isDebugBuild();

	ScuttleConfiguration getConfiguration(EntityManager db, String key);

	void setConfiguration(EntityManager db, String key, String value);

	List<? extends ScuttleConfiguration> getConfiguration(EntityManager db);

	void createUser(EntityManager db, String loginName, String role,
			String password);

	ScuttleUser getUserWithPrivileges(EntityManager db, String loginName);

	ScuttleUser getUser(EntityManager db, String loginName);
}
