package com._7aske.grain.fertilizer.web.server.tomcat;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.fertilizer.web.server.tomcat.adapter.HttpServletRequestAdapter;
import com._7aske.grain.fertilizer.web.server.tomcat.adapter.HttpServletResponseAdapter;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.authentication.provider.HttpRequestAuthenticationProviderStrategy;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.web.controller.exceptionhandler.ExceptionControllerHandler;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.requesthandler.handler.runner.HandlerRunner;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Single servlet entry point that handles requests and forwards them to the
 * Grain Web framework.
 */
class DispatcherServlet extends HttpServlet implements RequestHandler {
    private static final String DEFAULT_SERVLET_PATH = "/";
    public static final String SERVLET_NAME = "DispatcherServlet";
    private final HandlerRunner handlerRunner;
    private final ExceptionControllerHandler errorHandler;
    private final HttpRequestAuthenticationProviderStrategy provider;
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    public DispatcherServlet(HandlerRunner handlerRunner, ExceptionControllerHandler errorHandler, HttpRequestAuthenticationProviderStrategy provider) {
        this.handlerRunner = handlerRunner;
        this.errorHandler = errorHandler;
        this.provider = provider;
    }

    @Override
    public String getServletName() {
        return SERVLET_NAME;
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        logger.debug("Servicing {} {}", req.getMethod(), req.getRequestURI());

        HttpRequest request = new HttpServletRequestAdapter(req);
        HttpResponse response = new HttpServletResponseAdapter(res);

        Authentication authentication = provider.getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            handle(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            handlerRunner.handle(request, response);
        } catch (Exception ex) {
            Throwable actual = ex.getCause() != null ? ex.getCause() : ex;
            errorHandler.handle(actual, request, response);
        }
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public @NotNull String getPath() {
        return DEFAULT_SERVLET_PATH;
    }


    @Override
    public Collection<HttpMethod> getMethods() {
        return List.of(HttpMethod.values());
    }
}
