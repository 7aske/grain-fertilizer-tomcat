package com._7aske.grain.fertilizer.web.server.tomcat.adapter;

import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.Session;
import com._7aske.grain.web.http.session.SessionToken;
import jakarta.servlet.http.HttpSession;

import static com._7aske.grain.web.http.session.SessionConstants.SESSION_COOKIE_NAME;

/**
 * Adapts the {@link HttpSession} to Grain {@link Session}.
 */
public class SessionAdapter implements Session {
    private final HttpSession session;

    public SessionAdapter(HttpSession session) {
        this.session = session;
    }

    @Override
    public SessionToken getToken() {
        return new Cookie(SESSION_COOKIE_NAME, session.getId());
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public Object get(Object key) {
        return session.getAttribute((String) key);
    }

    @Override
    public void put(Object key, Object value) {
        session.setAttribute((String) key, value);
    }

    @Override
    public void remove(Object key) {
        session.removeAttribute((String) key);
    }

    @Override
    public boolean containsKey(Object key) {
        return session.getAttribute((String) key) != null;
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }
}
