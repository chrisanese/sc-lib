package de.fu.mi.scuttle.lib.util.test;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import de.fu.mi.scuttle.lib.ScuttleConfiguration;
import de.fu.mi.scuttle.lib.ScuttleMeta;
import de.fu.mi.scuttle.lib.ScuttleModule;
import de.fu.mi.scuttle.lib.ScuttleNoPermissionException;
import de.fu.mi.scuttle.lib.ScuttleUpdateVisitor;
import de.fu.mi.scuttle.lib.ScuttleUpdater;
import de.fu.mi.scuttle.lib.ScuttleUser;
import de.fu.mi.scuttle.lib.persistence.EntityManager;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleServlet;

public class ScuttleServletMockup implements ScuttleServlet {

	private final String home;

	public ScuttleServletMockup(final String home) {
		this.home = home;
	}

	public ScuttleServletMockup() {
		this.home = ".";
	}

	@Override
	public JSONObject getConfig() {
		return new JSONObject();
	}

	@Override
	public void check(final ScuttleRequest request, final String privilege)
			throws ScuttleNoPermissionException {

	}

	@Override
	public <T extends ScuttleModule> T getModule(final Class<T> clazz) {
		return null;
	}

	@Override
	public void update(final ScuttleUpdater updater) {

	}

	@Override
	public void update(final ScuttleUpdateVisitor<?> updater) {

	}

	@Override
	public EntityManager db() {
		return null;
	}

	@Override
	public String getRealPath(final String path) {
		return new File(home + "/" + path).getAbsolutePath();
	}

	@Override
	public String getConfigString(final String key, final String defaultValue) {
		return defaultValue;
	}

	@Override
	public String getConfigString(final String key) {
		return null;
	}

	@Override
	public ScuttleMeta getMeta() {
		return new ScuttleMeta() {

			@Override
			public String getReleaseDate() {
				return null;
			}

			@Override
			public String getVersion() {
				return null;
			}

			@Override
			public String getFirstPage() {
				return null;
			}

			@Override
			public ScuttleConfiguration getConfiguration(EntityManager db,
					String key) {
				return null;
			}

			@Override
			public void setConfiguration(EntityManager db, String key,
					String value) {
				
			}

			@Override
			public List<? extends ScuttleConfiguration> getConfiguration(
					EntityManager db) {
				return null;
			}

			@Override
			public void createUser(EntityManager db, String loginName,
					String role, String password) {
				
			}

			@Override
			public ScuttleUser getUserWithPrivileges(EntityManager db,
					String loginName) {
				return null;
			}

			@Override
			public ScuttleUser getUser(EntityManager db, String loginName) {
				return null;
			}

			@Override
			public boolean isDebugBuild() {
				return false;
			}
		};
	}
}
