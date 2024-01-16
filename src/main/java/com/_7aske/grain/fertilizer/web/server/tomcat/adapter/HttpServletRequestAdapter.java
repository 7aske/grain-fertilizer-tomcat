package com._7aske.grain.fertilizer.web.server.tomcat.adapter;


import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.multipart.Part;
import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.Session;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Adapts the {@link HttpServletRequest} to Grain {@link HttpRequest}.
 */
public class HttpServletRequestAdapter implements HttpRequest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpServletRequest request;

    public HttpServletRequestAdapter(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> stringSet = new HashSet<>();
        request.getAttributeNames().asIterator().forEachRemaining(stringSet::add);
        return stringSet;
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        request.setCharacterEncoding(encoding);
    }

    @Override
    public long getContentLength() {
        return request.getContentLength();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return null;
        }

        return value.split("\\s*,\\s*")[0];
    }

    @Override
    public Set<String> getParameterNames() {
        Set<String> stringSet = new HashSet<>();
        request.getParameterNames().asIterator().forEachRemaining(stringSet::add);
        return stringSet;
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return request.getProtocol();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public List<Locale> getLocales() {
        List<Locale> localeSet = new ArrayList<>();
        request.getLocales().asIterator().forEachRemaining(localeSet::add);
        return localeSet;
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return request.getRemotePort();
    }


    @Override
    public String getRequestId() {
        return request.getRequestId();
    }

    @Override
    public Cookie[] getCookies() {
        if (request.getCookies() == null) {
            return new Cookie[0];
        }

        return Arrays.stream(request.getCookies())
                .map(cookie -> {
                    Cookie c = new Cookie(cookie.getName(), cookie.getValue());
                    c.setDomain(cookie.getDomain());
                    c.setPath(cookie.getPath());
                    c.setMaxAge(cookie.getMaxAge());
                    c.setSecure(cookie.getSecure());
                    return c;
                })
                .toArray(Cookie[]::new);
    }

    @Override
    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public String[] getHeaders(String name) {
        Iterator<String> iterator = request.getHeaders(name).asIterator();
        List<String> stringList = new ArrayList<>();
        iterator.forEachRemaining(stringList::add);
        return stringList.toArray(new String[0]);
    }

    @Override
    public Set<String> getHeaderNames() {
        Iterator<String> iterator = request.getHeaderNames().asIterator();
        Set<String> stringSet = new HashSet<>();
        iterator.forEachRemaining(stringSet::add);
        return stringSet;
    }

    @Override
    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod());
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    @Override
    public String getPath() {
        return request.getRequestURI();
    }

    @Override
    public Session getSession(boolean create) {
        logger.info("HttpServletRequestAdapter.getSession({}) called", create);
        if (create) {
            return new SessionAdapter(request.getSession(true));
        }
        return new SessionAdapter(request.getSession());
    }

    @Override
    public Session getSession() {
        logger.info("HttpServletRequestAdapter.getSession() called");
        return new SessionAdapter(request.getSession(true));
    }

    @Override
    public Collection<Part> getParts() {
        try {
            return request.getParts()
                    .stream()
                    .<Part>map(PartAdapter::new)
                    .toList();
        } catch (IOException | ServletException e) {
            throw new GrainRuntimeException(e);
        }
    }

    @Override
    public Part getPart(String name) {
        try {
            jakarta.servlet.http.Part jakartaPart = request.getPart(name);
            if (jakartaPart == null) {
                return null;
            }
            return new PartAdapter(jakartaPart);
        } catch (IOException | ServletException e) {
            return null;
        }
    }

    @Override
    public boolean isTrailerFieldsReady() {
        throw new RuntimeException("Not implemented");
    }
}
