package de.fu.mi.scuttle.lib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.fu.mi.scuttle.lib.ScuttleBackendServlet.RequestHandler;
import de.fu.mi.scuttle.lib.util.PathUtil;
import de.fu.mi.scuttle.lib.web.ExceptionUtil;
import de.fu.mi.scuttle.lib.web.HttpCachingResponse;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleResponse;

class RequestHandlerImpl implements RequestHandler {

    private final ScuttleBackendServlet parent;

    RequestHandlerImpl(final ScuttleBackendServlet parent) {
        this.parent = parent;
    }

    /**
     * Handle a request.
     * 
     * (1) Check if a handler exists for the given path. (2) handle() the
     * request or if no handler exists error404. (3) doResponse() on the
     * response from handle().
     * 
     * If any exception occurs during handle() or doResponse() error505.
     * 
     * @param httpRequest
     *            The (raw) HTTP Request
     * @param httpResponse
     *            The (raw) HTTP Response
     */
    @Override
    public void handleRequest(
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse) {
        final String[] pathInfo = PathUtil.pathInfo(httpRequest
                .getPathInfo());
        parent.modulesRWLock.readLock().lock();
        try {
            final ScuttleModule handler = parent.modules.get(pathInfo[0]);
            final ScuttleRequest request = new ScuttleRequestImpl(
                    httpRequest, pathInfo[1]);
            if (handler != null) {
                try {
                    parent.tryLogin(request);

                    final long cacheTag = handler.cacheTag(request);

                    final boolean shouldBeCached = cacheTag != 0;
                    final HttpCachingResponse cachedResponse =
                            shouldBeCached
                                    ? parent.getCache().get(handler, cacheTag)
                                    : null;

                    ScuttleResponse response;
                    if (shouldBeCached) {
                        // response should be in the cache or needs to be
                        // inserted into the cache
                        if (cachedResponse != null) {
                            // it is in the cache, go fetch that
                            response = cachedResponse;
                        } else {
                            // it is not in the cache, we need to populate the
                            // cache.
                            ScuttleResponse cacheableResponse = null;
                            // calculate the response first
                            try {
                                cacheableResponse = handler.handle(request);
                                response = cacheableResponse;
                            } catch (final ScuttleNoPermissionException exc) {
                                throw exc;
                            } catch (final Exception exc) {
                                response = ExceptionUtil.handleException(exc);
                                httpResponse.setStatus(500);
                            } finally {
                                handler.done();
                            }
                            // now cache the response by writing it into a
                            // HttpCachingResponse
                            if (cacheableResponse != null) {
                                final HttpCachingResponse cachingResponse = new HttpCachingResponse();
                                // doResponse does not write to the actual http
                                // response, but to the cachingResponse.
                                cacheableResponse.doResponse(
                                        request.acceptsGzip(), cachingResponse);
                                cachingResponse.done();
                                // write the cached response to the cache
                                parent.getCache().populate(
                                        handler, cacheTag, cachingResponse);
                                // the cachingResponse can act as a response too
                                response = cachingResponse;
                            } else {
                                parent.error404(httpResponse);
                            }
                        }
                    } else {
                        // the response should not be cached, go ahead
                        try {
                            response = handler.handle(request);
                        } catch (final ScuttleNoPermissionException exc) {
                            throw exc;
                        } catch (final Exception exc) {
                            response = ExceptionUtil.handleException(exc);
                            httpResponse.setStatus(500);
                        } finally {
                            handler.done();
                        }
                    }
                    if (response != null) {
                        response.doResponse(
                                request.acceptsGzip(),
                                new HttpScuttleServletResponse(httpResponse));
                    } else {
                        parent.error404(httpResponse);
                    }

                } catch (final ScuttleLoginException exc) {
                    parent.loginError(httpResponse, exc);
                } catch (final ScuttleNoPermissionException exc) {
                    parent.error403(httpResponse, exc);
                } catch (final Exception exc) {
                    parent.error500(httpResponse, exc);
                } finally {
                    parent.closeEntityManager();
                }
            } else {
                parent.error404(httpResponse);
            }
        } finally {
            parent.modulesRWLock.readLock().unlock();
        }
    }
}