package de.fu.mi.scuttle.lib;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.mail.MessagingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import de.fu.mi.scuttle.lib.ScuttleLoginException.LoginException;
import de.fu.mi.scuttle.lib.ScuttleNoPermissionException.NoPermissionException;
import de.fu.mi.scuttle.lib.persistence.EntityManager;
import de.fu.mi.scuttle.lib.persistence.EntityManagerWrapper;
import de.fu.mi.scuttle.lib.persistence.PersistenceUtil;
import de.fu.mi.scuttle.lib.util.Mail;
import de.fu.mi.scuttle.lib.util.MailMessage;
import de.fu.mi.scuttle.lib.util.MailProtocol;
import de.fu.mi.scuttle.lib.web.AbstractScuttleServlet;
import de.fu.mi.scuttle.lib.web.Crucial;
import de.fu.mi.scuttle.lib.web.Deferred;
import de.fu.mi.scuttle.lib.web.ExceptionUtil;
import de.fu.mi.scuttle.lib.web.MountPoint;
import de.fu.mi.scuttle.lib.web.ResponseCache;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleServlet;

/**
 * The Scuttle main servlet.
 * 
 * @author Julian Fleischer
 * @since 2015-07-30
 */
public class ScuttleBackendServlet extends AbstractScuttleServlet implements
		ScuttleServlet {

	private static final long serialVersionUID = -4214133380506978705L;

	final ReadWriteLock modulesRWLock = new ReentrantReadWriteLock();
	final Map<String, ScuttleModule> modules = new HashMap<>();
	private Map<Class<? extends ScuttleModule>, ScuttleModule> modulesByClass = new HashMap<>();
	private ResponseCache cache;

	private volatile EntityManagerFactory emf;
	private ThreadLocal<EntityManager> em = new ThreadLocal<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	interface RequestHandler {
		void handleRequest(HttpServletRequest req, HttpServletResponse resp);
	}

	private class BrokenConfigRequestHandler implements RequestHandler {
		public BrokenConfigRequestHandler() {
			// does nothing, but is public
		}

		@Override
		public void handleRequest(final HttpServletRequest req,
				final HttpServletResponse resp) {
			error500(resp, getConfigurationProblems());
		}
	}

	private class EmptyRequestHandler implements RequestHandler {
		public EmptyRequestHandler() {
			// does nothing, but is public
		}

		@Override
		public void handleRequest(final HttpServletRequest req,
				final HttpServletResponse resp) {
			error404(resp);
		}
	}

	private RequestHandler requestHandler = new EmptyRequestHandler();

	@Override
	public void init() throws ServletException {
		System.setProperty("java.awt.headless", "true");

		super.init();
		initPersistence();
		//intiConfigFromDatabase();
		initModules();
		initCache();

		final List<Exception> exceptions = getConfigurationProblems();
		if (exceptions.size() > 0) {
			final StringWriter writer = new StringWriter();
			try {
				ExceptionUtil.printExceptions(writer, exceptions);
				logger().warn(writer.toString());
			} catch (final IOException exc) {
				throw new RuntimeException(exc);
			}
			requestHandler = new BrokenConfigRequestHandler();
		} else {
			requestHandler = new RequestHandlerImpl(this);
		}
	}

	/**
	 * Initializes the {@link EntityManagerFactory} in {@link #emf}.
	 */
	private void initPersistence() {
		final String jdbcDriver = getConfig().optString("jdbcDriver");
		final String jdbcUser = getConfig().optString("jdbcUser");
		final String jdbcPassword = getConfig().optString("jdbcPassword");
		final String jdbcUrl = getConfig().optString("jdbcUrl");

		final String persistenceUnitName = getServletContext()
				.getInitParameter("scuttlePersistenceUnit");
		if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
			logger.error("Could not find init-parameter 'scuttlePersistenceUnit'.");
			return;
		}

		final Properties properties = PersistenceUtil.getProperties(jdbcDriver,
				jdbcUser, jdbcPassword, jdbcUrl);

		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName,
					properties);

			em = EntityManagerWrapper.wrap(emf.createEntityManager());

			final ScuttleConfiguration dbVersion = getMeta().getConfiguration(
					em, "db-version");
			if (dbVersion == null) {
				throw new ScuttleConnectivityException(DbConfig.DB_VERSION);
			} else if (!DbConfig.DB_VERSION.equals(dbVersion.getValue())) {
				throw new ScuttleConnectivityException(DbConfig.DB_VERSION,
						dbVersion.getValue());
			}
		} catch (final ScuttleConnectivityException exc) {
			reportConfigurationProblem(exc);
		} catch (final Exception exc) {
			reportConfigurationProblem(new ScuttleConnectivityException(exc));
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	/**
	 * Updates the configuration with items from the database.
	 */
	/*private void intiConfigFromDatabase() {
		//final List<? extends ScuttleConfiguration> dbConfig = getMeta()
			//	.getConfiguration(db());

		for (final ScuttleConfiguration conf : dbConfig) {
			final String key = conf.getKey();
			final String value = conf.getValue();

			try {
				getConfig().put(key, value);
			} catch (final JSONException exc) {
				reportConfigurationProblem(exc);
			}
		}
	}*/

	/**
	 * Initializes all modules, see {@link ScuttleModule}.
	 */
	private void initModules() {
		String modules = getInitParameter("scuttleHandlers");

		if (modules == null) {
			modules = "";
		}
		final String[] handlerClasses = modules.split(" *[\n,;]+ *");
		for (final String handlerClass : handlerClasses) {
			final String[] handler = handlerClass.matches(" *[=:]+ *") ? handlerClass
					.split(" *[=:]+ *") : new String[] { null, handlerClass };

			String name = handler[0] != null ? handler[0].trim() : null;
			final String className = handler[1].trim();
			try {
				@SuppressWarnings("unchecked")
				final Class<? extends ScuttleModule> clazz = (Class<? extends ScuttleModule>) Class
						.forName(className);
				if (clazz.isAnnotationPresent(Crucial.class)
						&& clazz.isAnnotationPresent(Deferred.class)) {
					throw new ScuttleIllDefinedModuleException(clazz,
							"A module must not declare @Crucial and @Deferred at the same time.");
				}
				if (name == null) {
					if (clazz.isAnnotationPresent(MountPoint.class)) {
						name = clazz.getAnnotation(MountPoint.class).value();
					} else {
						throw new ScuttleIllDefinedModuleException(clazz,
								"The module does not define a @MountPoint.");
					}
				}
				ScuttleModule h;
				try {
					final Constructor<?> c = findConstructor(clazz);
					h = (ScuttleModule) c.newInstance(this);
				} catch (final NoSuchMethodException exc) {
					h = clazz.newInstance();
				}
				this.modules.put(name, h);
				this.modulesByClass.put(clazz, h);
			} catch (final Exception exc) {
				reportConfigurationWarning(new ScuttleHandlerInitializationException(
						exc, name, className));
			}

		}

		for (final Entry<String, ScuttleModule> entry : this.modules.entrySet()) {
			final ScuttleModule module = entry.getValue();
			if (module.getClass().isAnnotationPresent(Deferred.class)) {
				try {
					module.loaded();
				} catch (final Exception exc) {
					if (module.getClass().isAnnotationPresent(Crucial.class)) {
						reportConfigurationProblem(new ScuttleHandlerInitializationException(
								exc, entry.getKey(), module.getClass()
										.getName()));
					} else {
						reportConfigurationWarning(new ScuttleHandlerInitializationException(
								exc, entry.getKey(), module.getClass()
										.getName()));
					}
				}
			}
		}
	}

	private Constructor<?> findConstructor(
			final Class<? extends ScuttleModule> clazz)
			throws NoSuchMethodException {
		for (final Constructor<?> c : clazz.getConstructors()) {
			final Class<?>[] params = c.getParameterTypes();
			if (params == null || params.length != 1) {
				continue;
			}
			if (params[0].isAssignableFrom(getClass())) {
				return c;
			}
		}
		throw new NoSuchMethodException();
	}

	private void initCache() {
		cache = new ResponseCache(modules.values());
	}

	void tryLogin(final ScuttleRequest request) throws ScuttleLoginException {
		if (request.get("logout") != null) {
			request.getSession().remove("username");
		}
		final String loginName = request.get("loginName");
		if (Strings.isNullOrEmpty(loginName)) {
			return;
		}
		final String loginPassword = request.get("loginPassword");
		if (Strings.isNullOrEmpty(loginPassword)) {
			throw new ScuttleLoginException(LoginException.NO_PASSWORD_GIVEN);
		}
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException exc) {
		}
		final ScuttleUser user = getMeta().getUser(db(), loginName);
		if (user == null) {
			throw new ScuttleLoginException(LoginException.UNKNOWN_USER);
		}
		if (!user.getPassword().check(loginPassword)) {
			throw new ScuttleLoginException(
					LoginException.PASSWORDS_DO_NOT_MATCH);
		}
		request.getSession().put("username", loginName);
	}

	@Override
	public void check(final ScuttleRequest request, final String privilege)
			throws ScuttleNoPermissionException {
		final String username = request.getSession().getString("username");

		if (Strings.isNullOrEmpty(username)) {
			throw new ScuttleNoPermissionException(
					NoPermissionException.NO_SESSION);
		}
		final EntityManager em = db();
		em.clear();
		final ScuttleUser user = getMeta().getUserWithPrivileges(em, username);
		if (user == null) {
			throw new ScuttleNoPermissionException(
					NoPermissionException.SESSION_USER_NOT_IN_DATABASE);
		}
		if ("admin".equals(user.getRole())) {
			return;
		}
		if (user.getPrivileges().contains(privilege)) {
			return;
		}

		throw new ScuttleNoPermissionException(
				NoPermissionException.USER_LACKS_PRIVILEGE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ScuttleModule> T getModule(final Class<T> clazz) {
		return (T) modulesByClass.get(clazz);
	}

	/**
	 * Retrieve the response cache of this servlet.
	 * 
	 * @return The Response Cache.
	 */
	public ResponseCache getCache() {
		return cache;
	}

	/**
	 * Sends an email message.
	 * 
	 * @param message
	 *            The email message.
	 * @throws MessagingException
	 *             If the message could not be delivered.
	 */
	public void sendMail(final MailMessage message) throws MessagingException {

		final String host = getConfigString("mail.server", "localhost");
		final String username = getConfigString("mail.username");
		final String password = getConfigString("mail.password");
		final String protocol = getConfigString("mail.protocol", "smtp");

		final String from = getConfigString("mail.from");
		if (message.getFrom() == null) {
			message.setFrom(from);
		}

		Mail.sendMail(host, username, password, MailProtocol.forName(protocol),
				message);
	}

	@Override
	public void destroy() {
		super.destroy();
		emf.close();
	}

	@Override
	public void update(final ScuttleUpdater updater) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				modulesRWLock.writeLock().lock();
				try {
					updater.update(modules);
				} finally {
					modulesRWLock.writeLock().unlock();
				}
			}
		}).start();
	}

	@Override
	public void update(final ScuttleUpdateVisitor<?> updater) {
		update(new ScuttleUpdater() {
			@Override
			public void update(final Map<String, ScuttleModule> modules) {
				for (final ScuttleModule handler : modules.values()) {
					updater.doUpdate(handler);
				}
			}
		});
	}

	@Override
	public EntityManager db() {
		EntityManager e = em.get();
		if (e == null) {
			e = EntityManagerWrapper.wrap(emf.createEntityManager());
			em.set(e);
		}
		return e;
	}

	/**
	 * Closes an open entity manager (if there is one) and explicitly rolls back
	 * all transactions left undone (if there are some).
	 */
	void closeEntityManager() {
		final EntityManager e = em.get();
		if (e != null) {
			try {
				if (e.getTransaction().isActive()) {
					e.getTransaction().rollback();
				}
			} catch (final Exception exc) {
				logger.warn(
						"Failed rolling back a transaction left uncommitted.",
						exc);
			} finally {
				try {
					em.remove();
				} catch (final Exception exc) {
					logger.warn(
							"Failed removing a thread-local EntityManager.",
							exc);
				} finally {
					try {
						e.close();
					} catch (final RuntimeException exc) {
						logger.warn("Failed finally closing an EntityManager.",
								exc);
					}
				}
			}
		}
	}

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		try {
			requestHandler.handleRequest(req, resp);
		} catch (final Error e) {
			e.printStackTrace(resp.getWriter());
		}
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		requestHandler.handleRequest(req, resp);
	}

	/**
	 * Throw an HTTP 500 Internal Server Error.
	 */
	void error500(final HttpServletResponse resp, final Exception exc) {
		try {
			resp.setContentType("text/plain");
			exc.printStackTrace(resp.getWriter());
		} catch (final IOException e) {
			// This really should not happen,
			// but if it happens, log the error.
			logger.warn("IOException while handling error500.", e);
		}
	}

	/**
	 * Report an HTTP 500 Internal Server Error, reporting multiple Exceptions.
	 * 
	 * @since 2013-10-18
	 */
	void error500(final HttpServletResponse resp, final List<Exception> exc) {
		try {
			ExceptionUtil.error500(resp, exc);
		} catch (final IOException e) {
			// This really should not happen,
			// but if it happens, log the error.
			logger.warn("IOException while handling error500.", e);
		}
	}

	/**
	 * Report an HTTP 403 Forbidden Error.
	 */
	void error403(final HttpServletResponse resp,
			final ScuttleNoPermissionException exc) {
		try {
			ExceptionUtil.error403(resp, exc);
		} catch (final IOException e) {
			// This really should not happen,
			// but if it happens, log the error.
			logger.warn("IOException while handling error403.", e);
		}
	}

	/**
	 * Report an HTTP 404 Not Found Error.
	 */
	void error404(final HttpServletResponse resp) {
		try {
			ExceptionUtil.error404(resp);
		} catch (final Exception e) {
			// This really should not happen,
			// but if it happens, log the error.
			logger.warn("Exception while handling error404.", e);
		}
	}

	/**
	 * Report a login error.
	 * 
	 * @since 2013-10-26
	 */
	void loginError(final HttpServletResponse resp,
			final ScuttleLoginException exc) {
		try {
			ExceptionUtil.loginError(resp, exc);

		} catch (final Exception e) {
			// This really should not happen,
			// but if it happens, log the error.
			logger.warn("Exception while handling error404.", e);
		}
	}

	@Override
	protected Logger logger() {
		return logger;
	}
}
