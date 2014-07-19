package de.fu.mi.scuttle.lib.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;

import com.google.common.base.Charsets;

import de.fu.mi.scuttle.lib.ScuttleMeta;
import de.fu.mi.scuttle.lib.util.concurrent.JobQueue;

/**
 * The scuttle base servlet (provides basic configuration)
 * 
 * @author Julian Fleischer
 * @since 2015-07-30
 */
public abstract class AbstractScuttleServlet extends HttpServlet implements
        ScuttleSimpleServlet {

    /**
     * The legacy serial version uid.
     */
    private static final long serialVersionUID = -3644911880352068093L;

    /**
     * The configuration of this servlet.
     */
    private JSONObject config = new JSONObject();

    private List<String> configFilesTried = new ArrayList<String>();

    private List<Exception> configurationProblems = new ArrayList<Exception>();

    private List<Exception> configurationWarnings = new ArrayList<Exception>();

    private ServletContext context = null;
    
    private ScuttleMeta meta;

    private JobQueue jobQueue = null;

    @Override
    public JSONObject getConfig() {
        return config;
    }

    @Override
    public String getConfigString(final String key) {
        return config.optString(key);
    }

    @Override
    public String getConfigString(final String key, final String defaultValue) {
        return config.optString(key, defaultValue);
    }


	/**
	 * Initializes the config class which handles specific tasks.
	 */
	private void initMeta() {
		String configClassName = getServletContext().getInitParameter(
				"scuttleConfigClass");
		try {
			Class<?> clazz = Class.forName(configClassName);
			this.meta = (ScuttleMeta) clazz.newInstance();
		} catch (ClassNotFoundException exc) {
			reportConfigurationProblem(exc);
		} catch (ClassCastException exc) {
			reportConfigurationProblem(exc);
		} catch (IllegalAccessException exc) {
			reportConfigurationProblem(exc);
		} catch (InstantiationException exc) {
			reportConfigurationProblem(exc);
		}
	}

    
    @Override
    public void init() throws ServletException {
        this.context = getServletContext();

        initConfig();
        initJobQueue();
        initMeta();
    }

    private void initJobQueue() {
        jobQueue = new JobQueue();
        jobQueue.start();
    }

    private void initConfig() {
        try (
                final InputStream s = getConfigStream();
                final Reader r = new InputStreamReader(s, Charsets.UTF_8)) {
            final JSONTokener t = new JSONTokener(r);
            config = new JSONObject(t);
        } catch (final Exception exc) {
            configurationProblems.add(exc);
        }
    }

    private InputStream getConfigStream() {
        File configFile = null;
        try {
            // BOGUS: Is context...initParameter different from initParameter?
            configFile = checkFile(getInitParameter("scuttleConfig"));
            if (configFile != null) {
                return new FileInputStream(configFile);
            }
            configFile = checkFile(
                    getServletContext().getInitParameter("scuttleConfig"));
            if (configFile != null) {
                return new FileInputStream(configFile);
            }
            configFile = checkFile(System.getProperty("scuttle.config"));
            if (configFile != null) {
                return new FileInputStream(configFile);
            }
            configFile = checkFile(System.getenv("SCUTTLE_CONFIG"));
            if (configFile != null) {
                return new FileInputStream(configFile);
            }
            File baseDir = new File(getServletContext()
                    .getRealPath("index.htm"));
            while ((baseDir = baseDir.getParentFile()) != null) {
                configFile = checkFile(baseDir.getAbsolutePath()
                        + "/mvs.conf");
                if (configFile != null) {
                    return new FileInputStream(configFile);
                }
            }
            throw new RuntimeException(
                    "No configuration file found, using default configuration");
        } catch (final Exception exc) {
            configurationProblems.add(exc);
        }
        return getClass().getResourceAsStream("scuttle.default.conf");
    }

    /**
     * Returns the job queue.
     * 
     * @return The job queue of this servlet.
     */
    public JobQueue getJobQueue() {
        return jobQueue;
    }

    /**
     * Checks a possible config file for reabability or returns null if this is
     * not possible (for example if the files does not exist).
     * 
     * @param filename
     *            The pathname of the file to open.
     * @return A File object if the file exists and can be read or null if it
     *         does not.
     */
    protected File checkFile(final String filename) {
        configFilesTried.add(filename);
        if (filename != null) {
            final File f = new File(filename);
            if (f.exists() && f.canRead()) {
                return f;
            }
        }
        return null;
    }

    @Override
    public ScuttleMeta getMeta() {
    	return meta;
    }
    
    @Override
    public String getRealPath(final String path) {
        if (context != null) {
            return context.getRealPath(path);
        }
        return "./" + path;
    }

    protected void reportConfigurationProblem(final Exception exc) {
        this.configurationProblems.add(exc);
    }

    protected void reportConfigurationWarning(Exception exc) {
        this.configurationWarnings.add(exc);
    }

    public List<Exception> getConfigurationProblems() {
        return Collections.unmodifiableList(configurationProblems);
    }

    public List<Exception> getConfigurationWarnings() {
        return Collections.unmodifiableList(configurationWarnings);
    }

    protected List<String> getConfigFilesTried() {
        return Collections.unmodifiableList(configFilesTried);
    }

    @Override
    public void destroy() {
        if (jobQueue != null) {
            jobQueue.stop();
        }
        super.destroy();
    }

    protected abstract Logger logger();
}
