package de.fu.mi.scuttle.lib;

import de.fu.mi.scuttle.lib.web.JSONResponse;
import de.fu.mi.scuttle.lib.web.PDFResponse;
import de.fu.mi.scuttle.lib.web.PlainResponse;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleResponse;
import de.fu.mi.scuttle.lib.web.XMLResponse;

/**
 * A handler for a specific route.
 * 
 * @author Julian Fleischer
 */
public interface ScuttleModule {

    /**
     * Handles a {@link ScuttleRequest} and produces a {@link ScuttleResponse}.
     * 
     * @param req
     *            The Request.
     * @return A Response - in most cases this is a {@link JSONResponse}, a
     *         {@link PlainResponse}, a {@link PDFResponse}, or an
     *         {@link XMLResponse}.
     * @throws ScuttleNoPermissionException
     *             If the handler performs a validation of the user and her
     *             privileges and the user does not actually have the necessary
     *             privilege(s).
     * @throws Exception
     *             Anything might happen.
     */
    ScuttleResponse handle(ScuttleRequest req) throws Exception;

    /**
     * Invoked by the Servlet when the request is done - useful for e.g. making
     * sure that all thread local resources are closed.
     * 
     * @throws Exception
     *             This method may throw any exception.
     */
    void done() throws Exception;

    /**
     * Generate a cache tag for a certain request.
     * 
     * @param req
     *            The request.
     * @return 0 if the page should not be cached or a value different from zero
     *         which uniquely identifies the response.
     */
    long cacheTag(ScuttleRequest req);

    /**
     * This method is invoked when all modules are loaded.
     * 
     * When a module is loaded, its constructor will be invoked. It is however
     * in no way guaranteed, that other modules have been loaded yet. However,
     * when loaded() is invoked, all modules have been loaded and their
     * constructors were invoked.
     * 
     * @throws Exception
     *             This method may throw any exception.
     */
    void loaded() throws Exception;
}
