package de.fu.mi.scuttle.lib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.fu.mi.scuttle.lib.util.MimeTypeUtil;
import de.fu.mi.scuttle.lib.util.PathUtil;
import de.fu.mi.scuttle.lib.web.AbstractScuttleServlet;

/**
 * Serves static content except for index.htm, which is a preprocessed mustache
 * template. This servlet is installed at the context path, such that it catches
 * all requests.
 * 
 * @author Julian Fleischer
 * @since 2013-11-01
 */
public class ScuttleIndexHtml extends AbstractScuttleServlet {

    /**
     * Legacy serial version uid.
     */
    private static final long serialVersionUID = -8060401094047947969L;

    /**
     * The starting point.
     */
    private String startPage = "";

    /**
     * These are not served, but instead index.htm is fetched.
     */
    private Set<String> blacklist = new HashSet<>();
    {
        blacklist.add("js"); // are fetched by the Scripts- and Lib-Handler
        blacklist.add("templates"); // are fetched by the Templates-Handler
        blacklist.add("WEB-INF"); // must not be fetched
        blacklist.add("index.htm"); // is always served and preprocessed using
                                    // mustache
    }

    private Map<String, String> defaultValues = new HashMap<>();
    {
        defaultValues.put("title", "Scuttle FU/MI");
        defaultValues.put("loading", "Lade...");
        defaultValues.put("preLoading",
                "Lade... (JavaScript muss aktiviert sein - ist das der Fall?");
    }

    private static void send(
            final File file,
            final HttpServletResponse resp) throws IOException {
        final String extension = Files.getFileExtension(file.getPath())
                .toLowerCase();
        resp.setContentType(MimeTypeUtil
                .mimeTypeForFileExtension(extension));
        resp.setContentLength((int) file.length());
        try (final OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }

    /**
     * The mustache template which is used for generating index.htm
     */
    private Mustache indexMustache = null;

    private String contextPath = null;

    private File getFile(final String path) {
        return new File(getServletContext().getRealPath(path));
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.startPage = getMeta().getFirstPage();
        try (final Reader reader = Files.newReader(getFile("index.htm"),
                Charsets.UTF_8)) {
            contextPath = getServletContext().getContextPath();
            final DefaultMustacheFactory factory = new DefaultMustacheFactory();
            indexMustache = factory.compile(reader, "module");
        } catch (final Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(
            final HttpServletRequest req,
            final HttpServletResponse resp)
            throws ServletException, IOException {

        final String url = req.getPathInfo();
        final String url2 = url + ":";
        final String[] path = url2.split(":", 2);
        final String[] pathInfo = PathUtil.pathInfo(path[0]);

        if (pathInfo[0].isEmpty()) {
            resp.sendRedirect(contextPath + startPage);
        } else {
            handleRequest(path[0], pathInfo, url, resp);
        }
    }

    private void handleRequest(
            final String path,
            final String[] pathInfo,
            final String url,
            final HttpServletResponse resp) throws IOException {

        final File file = getFile(path);

        if (!blacklist.contains(pathInfo[0]) && file.exists()
                && file.isFile() && file.canRead()) {

            send(file, resp);

        } else {

            final Map<String, String> values = new HashMap<>();
            values.put("path", url);
            values.put("path0", pathInfo[0]);
            values.put("path1", pathInfo[1]);
            values.put("context", getServletContext().getContextPath());
            values.put("preLoading", "Lade... JavaScript muss aktiviert sein!");
            values.put("loading", "Lade...");

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html; charset=UTF-8");

            try (final Writer w = resp.getWriter()) {
                indexMustache.execute(resp.getWriter(), values);
            }
        }
    }

	@Override
	protected Logger logger() {
		// TODO Auto-generated method stub
		return null;
	}
}
