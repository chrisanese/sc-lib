package de.fu.mi.scuttle.lib;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import de.fu.mi.scuttle.lib.persistence.EntityManager;
import de.fu.mi.scuttle.lib.persistence.EntityManagerWrapper;
import de.fu.mi.scuttle.lib.persistence.PersistenceUtil;
import de.fu.mi.scuttle.lib.util.Pair;
import de.fu.mi.scuttle.lib.web.AbstractScuttleServlet;

/**
 * The Scuttle installation assistant.
 * 
 * @author Julian Fleischer
 */
public class ScuttleInstallServlet extends AbstractScuttleServlet {

	/**
	 * Possible outcomes of the setup.
	 */
	private static enum SetupResult {
		OKAY, DRIVER_MISSING, NO_PERSISTENCE_UNIT, EMF_FAILED, DB_CONNECTION_ERROR, SOME_EXCEPTION, SCHEMA_NOT_EMPTY, SCHEMA_CREATION_FAILED, CUSTOM_INSTALLATION_FAILED, COULD_NOT_DELETE_OLD_TABLES
	}

	private static final long serialVersionUID = 6229966880853759399L;
	private Lock lock = new ReentrantLock();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init() throws ServletException {
		super.init();
	}

	/**
	 * Check if scuttle is already installed. This is the case if the
	 * configuration can be used to connect to a database and this database
	 * contains the key/value pair <code>db-version</code> in the
	 * <code>Configuration</code> table and it matches
	 * {@link DbConfig#DB_VERSION}.
	 * 
	 * @return Whether scuttle is functioning with the current configuration or
	 *         not.
	 */
	private boolean isInstalledAlready() {
		boolean installed = true;
		try {
			final String u = getServletContext().getInitParameter(
					"scuttlePersistenceUnit");

			final Properties p = new Properties();
			p.setProperty("javax.persistence.jdbc.driver", getConfig()
					.optString("jdbcDriver", ""));
			p.setProperty("javax.persistence.jdbc.user",
					getConfig().optString("jdbcUser", ""));
			p.setProperty("javax.persistence.jdbc.password", getConfig()
					.optString("jdbcPassword", ""));
			p.setProperty("javax.persistence.jdbc.url",
					getConfig().optString("jdbcUrl", ""));

			final EntityManagerFactory emf = Persistence
					.createEntityManagerFactory(u, p);
			final EntityManager em = EntityManagerWrapper.wrap(emf
					.createEntityManager());

			final ScuttleConfiguration s = getMeta().getConfiguration(em,
					"db-version");
			if (s == null) {
				installed = false;
			} else {
				installed = DbConfig.DB_VERSION.equals(s.getValue());
			}

			em.close();
			emf.close();
		} catch (final Exception exc) {
			installed = false;
		}
		return installed;
	}

	private void renderTemplate(final String templateName,
			final HttpServletResponse resp, final Map<String, Object> scope)
			throws IOException {
		final MustacheFactory mf = new DefaultMustacheFactory();

		final InputStream res = getClass().getResourceAsStream(templateName);
		final InputStreamReader reader = new InputStreamReader(res,
				Charsets.UTF_8);
		final Mustache template = mf.compile(reader, templateName);
		reader.close();

		final String jdbcUser = getConfig().optString("jdbcUser", "");
		final String jdbcUrl = getConfig().optString("jdbcUrl", "");
		final Pattern p = Pattern.compile("^jdbc:mysql://([^/]+)/([^\\?]+)");
		final Matcher m = p.matcher(jdbcUrl);
		String hostname = "<HOSTNAME>";
		String database = "<DATABASE>";
		if (m.lookingAt()) {
			hostname = m.group(1);
			database = m.group(2);
		}

		scope.put("jdbcDriver", getConfig().optString("jdbcDriver", ""));
		scope.put("jdbcUser", jdbcUser);
		scope.put("jdbcUrl", jdbcUrl);
		scope.put("hostname", hostname);
		scope.put("database", database);
		scope.put("adminUser", getConfig().optString("adminUser", "admin"));
		scope.put("timestamp", String.valueOf(System.currentTimeMillis()));

		template.execute(resp.getWriter(), scope);
	}

	private void renderTemplate(final String templateName,
			final HttpServletResponse resp) throws IOException {
		final Map<String, Object> scope = new HashMap<>();
		renderTemplate(templateName, resp, scope);
	}

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		if (isInstalledAlready()) {
			renderTemplate("already-installed.htm", resp);
		} else {
			renderTemplate("install.htm", resp);
		}
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		if (isInstalledAlready()) {
			renderTemplate("already-installed.htm", resp);
		} else {
			try {
				lock.lock();

				final Pair<SetupResult, ? extends Exception> result = installScuttle(req);

				final Map<String, Object> info = new HashMap<>();

				switch (result.fst()) {
				case DB_CONNECTION_ERROR:
					info.put("fail-db", result.snd().getMessage());
					break;

				case DRIVER_MISSING:
					info.put("fail-driver",
							getConfig().optString("jdbcDriver", ""));
					break;

				case EMF_FAILED:
					info.put("fail-emf", result.snd().getMessage());
					break;

				case NO_PERSISTENCE_UNIT:
					info.put("fail-persistence", true);
					break;

				case SOME_EXCEPTION:
					info.put("fail-some", result.snd().getMessage());
					break;

				case OKAY:
					try {
						final File configFile = new File(getServletContext()
								.getRealPath("../../mvs.conf"));
						final String configData = getConfig().toString(2);

						boolean autoInstallFail = false;
						if ("yes".equals(req
								.getParameter("scuttle-auto-install"))) {
							if (configFile.canWrite()) {
								try {
									final OutputStream stream = new FileOutputStream(
											configFile);
									final PrintWriter writer = new PrintWriter(
											stream);
									writer.println(configData);
									writer.close();
									stream.close();
									info.put("auto-installed", true);
								} catch (final IOException exc) {
									autoInstallFail = true;
								}
							} else {
								autoInstallFail = true;
							}
						}

						info.put("auto-install-fail", autoInstallFail);
						info.put("success", true);
						info.put("config", configData);
						info.put("config-file", configFile.getCanonicalPath());

					} catch (final JSONException exc) {
						throw new RuntimeException(exc);
					}
					break;

				case SCHEMA_NOT_EMPTY:
					info.put("fail-not-empty", true);
					break;

				case SCHEMA_CREATION_FAILED:
					info.put("fail-creation", result.snd().getMessage());
					break;

				case CUSTOM_INSTALLATION_FAILED:
					info.put("fail-custom-installation", result.snd()
							.getMessage());
					break;

				case COULD_NOT_DELETE_OLD_TABLES:
					info.put("fail-not-empty", true);
					break;

				default:
					break;
				}

				renderTemplate("installed.htm", resp, info);
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	protected Logger logger() {
		return logger;
	}

	private void customInstallers(final EntityManager em) throws Exception {
		final String installerConf = getInitParameter("scuttleInstallers");
		final String[] installers = installerConf.trim().split("[ \t\r\n]+");

		if (installers != null) {
			for (final String installerClassname : installers) {
				if (!Strings.isNullOrEmpty(installerClassname)) {
					final Class<?> clazz = Class.forName(installerClassname);
					final ScuttleInstaller installer = (ScuttleInstaller) clazz
							.newInstance();
					try {
						installer.install(getConfig(), em);
					} catch (final Exception exc) {
						exc.printStackTrace(System.err);
						throw exc;
					}
				}
			}
		}
	}

	private Pair<SetupResult, ? extends Exception> installScuttle(
			final HttpServletRequest req) {

		// Retrieve parameters
		String jdbcDriver = req.getParameter("scuttle-jdbc-driver");
		String jdbcUrl = req.getParameter("scuttle-jdbc-url");
		String jdbcUser = req.getParameter("scuttle-jdbc-user");
		String jdbcPassword = req.getParameter("scuttle-jdbc-password");

		final String adminUser = req.getParameter("scuttle-admin-user");
		final String adminPassword = req.getParameter("scuttle-admin-password");
		final String mysqlUser = req.getParameter("scuttle-mysql-user");
		final String mysqlPassword = req.getParameter("scuttle-mysql-password");

		// Sync with configuration (both directions)
		try {
			if (!Strings.isNullOrEmpty(jdbcDriver)) {
				getConfig().put("jdbcDriver", jdbcDriver);
			}
			jdbcDriver = getConfig().optString("jdbcDriver", "");
			if (!Strings.isNullOrEmpty(jdbcUser)) {
				getConfig().put("jdbcUser", jdbcUser);
			}
			jdbcUser = getConfig().optString("jdbcUser", "");
			if (!Strings.isNullOrEmpty(jdbcPassword)) {
				getConfig().put("jdbcPassword", jdbcPassword);
			}
			jdbcPassword = getConfig().optString("jdbcPassword", "");
			if (!Strings.isNullOrEmpty(jdbcUrl)) {
				getConfig().put("jdbcUrl", jdbcUrl);
			}
			jdbcUrl = getConfig().optString("jdbcUrl", "");
		} catch (final JSONException exc) {
			return pair(SetupResult.SOME_EXCEPTION, exc);
		}

		try {
			Class.forName(jdbcDriver);
		} catch (final ClassNotFoundException exc) {
			return pair(SetupResult.DRIVER_MISSING, exc);
		}

		if (!Strings.isNullOrEmpty(mysqlUser)
				&& !Strings.isNullOrEmpty(mysqlPassword)) {
			/*
			 * If admin credentials are given, use them to create the scuttle
			 * user and the scuttle database.
			 */
			final Pattern p = Pattern
					.compile("^(jdbc:mysql://[^/]+/)([^\\?]+)");
			final Matcher m = p.matcher(jdbcUrl);
			if (m.lookingAt()) {
				final String mysqlUrl = m.group(1);
				final String database = m.group(2);
				Connection connection = null;
				Statement stmt = null;
				try {
					connection = DriverManager.getConnection(mysqlUrl,
							mysqlUser, mysqlPassword);
					stmt = connection.createStatement();

					stmt.execute(String.format("DROP DATABASE IF EXISTS `%s`",
							database));
					stmt.execute(String
							.format("CREATE DATABASE `%s`", database));
					stmt.execute(String
							.format("GRANT ALL PRIVILEGES ON `%s`.* TO '%s'@'%s' IDENTIFIED BY '%s'",
									database, jdbcUser, "localhost",
									jdbcPassword));
					stmt.close();
				} catch (final Exception exc) {
					return pair(SetupResult.SOME_EXCEPTION, exc);
				} finally {
					if (stmt != null) {
						try {
							stmt.close();
						} catch (final SQLException exc) {
							logger.warn("Closing connection failed.", exc);
							throw new RuntimeException(exc);
						}
					}
					if (connection != null) {
						try {
							connection.close();
						} catch (final SQLException exc) {
							logger.warn("Closing connection failed.", exc);
							throw new RuntimeException(exc);
						}
					}
				}
			}
		}

		// Connect with database
		final String u = getServletContext().getInitParameter(
				"scuttlePersistenceUnit");
		if (Strings.isNullOrEmpty(u)) {
			return pair(SetupResult.NO_PERSISTENCE_UNIT, null);
		}

		final Properties p = new Properties();
		p.setProperty("javax.persistence.jdbc.driver", jdbcDriver);
		p.setProperty("javax.persistence.jdbc.user", jdbcUser);
		p.setProperty("javax.persistence.jdbc.password", jdbcPassword);
		p.setProperty("javax.persistence.jdbc.url", jdbcUrl);

		if (jdbcUrl.toLowerCase().contains("mysql")) {
			p.setProperty("eclipselink.ddl-generation.table-creation-suffix",
					"ENGINE=InnoDB DEFAULT CHARSET utf8 COLLATE utf8_unicode_ci");
		}

		EntityManagerFactory emf;
		EntityManager em;

		try {
			emf = Persistence.createEntityManagerFactory(u, p);
		} catch (final Exception exc) {
			return pair(SetupResult.EMF_FAILED, exc);
		}

		try {
			em = EntityManagerWrapper.wrap(emf.createEntityManager());
		} catch (final Exception exc) {
			return pair(SetupResult.DB_CONNECTION_ERROR, exc);
		}

		try {
			PersistenceUtil.dropTablesStartingWith(em, DbConfig.TABLE_PREFIX,
					"DROP TABLE `%s`", "SET FOREIGN_KEY_CHECKS=0;",
					"SET FOREIGN_KEY_CHECKS=1");
		} catch (final SQLException exc) {
			return pair(SetupResult.COULD_NOT_DELETE_OLD_TABLES, exc);
		}

		// Check installed tables
		final Set<String> tablesNeeded = new TreeSet<>(
				PersistenceUtil.getSchemaTableNames(em));
		final Set<String> tables = new TreeSet<>();

		try {
			final Connection connection = DriverManager.getConnection(jdbcUrl,
					jdbcUser, jdbcPassword);
			final DatabaseMetaData metaData = connection.getMetaData();

			final ResultSet result = metaData.getTables(null, null, "", null);
			while (result.next()) {
				tables.add(result.getString("TABLE_NAME"));
			}
			result.close();
			connection.close();
		} catch (final Exception exc) {
			return pair(SetupResult.DB_CONNECTION_ERROR, exc);
		}
		tables.retainAll(tablesNeeded);

		if (!tables.isEmpty()) {
			return pair(SetupResult.SCHEMA_NOT_EMPTY, null);
		}

		// Create Schema
		try {
			PersistenceUtil.createTables(em);
		} catch (final Exception exc) {
			return pair(SetupResult.SCHEMA_CREATION_FAILED, exc);
		}

		// Setup data or anything according to installers.
		try {
			customInstallers(em);
		} catch (final Exception exc) {
			return pair(SetupResult.CUSTOM_INSTALLATION_FAILED, exc);
		}

		// Create Admin user
		getMeta().createUser(em, adminUser, "admin", adminPassword);

		// Finally mark the installation as successful
		getMeta().setConfiguration(em, "db-version", DbConfig.DB_VERSION);

		em.close();
		emf.close();

		return pair(SetupResult.OKAY, null);
	}
}
