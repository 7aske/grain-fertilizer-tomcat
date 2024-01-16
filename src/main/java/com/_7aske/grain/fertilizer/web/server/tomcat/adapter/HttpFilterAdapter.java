package com._7aske.grain.fertilizer.web.server.tomcat.adapter;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.handler.proxy.SecurityHandlerProxy;
import com._7aske.grain.web.http.GrainRequestHandlerException;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Adapter that adapts {@link RequestHandler} to the {@link HttpFilter} jakarta
 * class and allows using concepts such as Grain {@link com._7aske.grain.web.requesthandler.middleware.Middleware}
 * as filters in Tomcat.
 */
public class HttpFilterAdapter extends HttpFilter implements RequestHandler {
    private final RequestHandler handler;
    private final Logger logger;

    public HttpFilterAdapter(RequestHandler handler) {
        this.handler = handler;
        this.logger = LoggerFactory.getLogger(HttpFilterAdapter.class);
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        logger.debug("Filtering {} {}", req.getMethod(), req.getRequestURI());

        HttpRequest request = new HttpServletRequestAdapter(req);
        HttpResponse response = new HttpServletResponseAdapter(resp);
        try {
            handle(request, response);
            chain.doFilter(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws GrainRequestHandlerException {
        handler.handle(request, response);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return handler.canHandle(request);
    }

    @Override
    public String getPath() {
        return handler.getPath();
    }

    @Override
    public Collection<HttpMethod> getMethods() {
        return handler.getMethods();
    }
}
