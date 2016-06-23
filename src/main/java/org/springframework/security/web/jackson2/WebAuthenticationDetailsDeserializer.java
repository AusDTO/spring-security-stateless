package org.springframework.security.web.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class WebAuthenticationDetailsDeserializer extends JsonDeserializer<WebAuthenticationDetails> {
    @Override
    public WebAuthenticationDetails deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String remoteAddress = node.get("remoteAddress").asText();
        String sessionId = node.get("sessionId") != null ? node.get("sessionId").asText() : null;
        return new WebAuthenticationDetails(new HttpServletRequest() {
            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(String name) {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(String name) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean create) {
                if (sessionId == null) {
                    return null;
                }
                return new HttpSession() {
                    @Override
                    public long getCreationTime() {
                        return 0;
                    }

                    @Override
                    public String getId() {
                        return sessionId;
                    }

                    @Override
                    public long getLastAccessedTime() {
                        return 0;
                    }

                    @Override
                    public ServletContext getServletContext() {
                        return null;
                    }

                    @Override
                    public void setMaxInactiveInterval(int interval) {

                    }

                    @Override
                    public int getMaxInactiveInterval() {
                        return 0;
                    }

                    /**
                     *
                     * @deprecated As of Version 2.1, this method is
                     *			deprecated and has no replacement.
                     *			It will be removed in a future
                     *			version of the Java Servlet API.
                     *
                     */
                    @Override
                    public HttpSessionContext getSessionContext() {
                        return null;
                    }

                    @Override
                    public Object getAttribute(String name) {
                        return null;
                    }

                    /**
                     * @deprecated As of Version 2.2, this method is
                     * 			replaced by {@link #getAttribute}.
                     *
                     * @param name        a string specifying the name of the object
                     *
                     * @return the object with the specified name
                     *
                     * @exception IllegalStateException    if this method is called on an
                     *					invalidated session
                     */
                    @Override
                    public Object getValue(String name) {
                        return null;
                    }

                    @Override
                    public Enumeration<String> getAttributeNames() {
                        return null;
                    }

                    /**
                     * @deprecated As of Version 2.2, this method is
                     * 			replaced by {@link #getAttributeNames}
                     *
                     * @return an array of <code>String</code>
                     *					objects specifying the
                     *					names of all the objects bound to
                     *					this session
                     *
                     * @exception IllegalStateException    if this method is called on an
                     *					invalidated session
                     */
                    @Override
                    public String[] getValueNames() {
                        return new String[0];
                    }

                    @Override
                    public void setAttribute(String name, Object value) {

                    }

                    /**
                     * @deprecated As of Version 2.2, this method is
                     * 			replaced by {@link #setAttribute}
                     *
                     * @param name            the name to which the object is bound;
                     *					cannot be null
                     *
                     * @param value            the object to be bound; cannot be null
                     *
                     * @exception IllegalStateException    if this method is called on an
                     *					invalidated session
                     */
                    @Override
                    public void putValue(String name, Object value) {

                    }

                    @Override
                    public void removeAttribute(String name) {

                    }

                    /**
                     * @deprecated As of Version 2.2, this method is
                     * 			replaced by {@link #removeAttribute}
                     *
                     * @param name                the name of the object to
                     *						remove from this session
                     *
                     * @exception IllegalStateException    if this method is called on an
                     *					invalidated session
                     */
                    @Override
                    public void removeValue(String name) {

                    }

                    @Override
                    public void invalidate() {

                    }

                    @Override
                    public boolean isNew() {
                        return false;
                    }
                };
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            /**
             *
             * @deprecated As of Version 2.1 of the Java Servlet
             *				API, use {@link #isRequestedSessionIdFromURL}
             *				instead.
             *
             */
            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(String username, String password) throws ServletException {

            }

            @Override
            public void logout() throws ServletException {

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(String name) throws IOException, ServletException {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return remoteAddress;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(String name, Object o) {

            }

            @Override
            public void removeAttribute(String name) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return null;
            }

            /**
             * @deprecated As of Version 2.1 of the Java Servlet API,
             * 			use {@link ServletContext#getRealPath} instead.
             */
            @Override
            public String getRealPath(String path) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }
        });
    }
}
