package au.gov.dto.springframework.security.web.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/CookieSecurityContextRepository.java
 */
public class CookieSecurityContextRepository implements SecurityContextRepository {
    private final TokenEncryption tokenEncryption;

    private AuthenticationSerializer authenticationSerializer = new JsonAuthenticationSerializer();
    private String authenticationCookieName = "authentication";
    private String authenticationCookiePath = null;
    private int authenticationCookieMaxAgeSeconds = 3600;

    public CookieSecurityContextRepository(TokenEncryption tokenEncryption) {
        Assert.notNull(tokenEncryption);
        this.tokenEncryption = tokenEncryption;
    }

    /**
     * Obtains the security context for the supplied request. For an unauthenticated user, an empty context
     * implementation should be returned. This method should not return null.
     * <p>
     * The use of the <tt>HttpRequestResponseHolder</tt> parameter allows implementations to return wrapped versions of
     * the request or response (or both), allowing them to access implementation-specific state for the request.
     * The values obtained from the holder will be passed on to the filter chain and also to the <tt>saveContext</tt>
     * method when it is finally called. Implementations may wish to return a subclass of
     * {@link SaveContextOnUpdateOrErrorResponseWrapper} as the response object, which guarantees that the context is
     * persisted when an error or redirect occurs.
     *
     * @param requestResponseHolder holder for the current request and response for which the context should be loaded.
     *
     * @return The security context which should be used for the current request, never null.
     */
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        requestResponseHolder.setResponse(new SaveToCookieResponseWrapper(request, response));
        Cookie authenticationCookie = getAuthenticationCookie(request);
        if (authenticationCookie == null) {
            return SecurityContextHolder.createEmptyContext();
        }
        String serialisedAuthentication = tokenEncryption.decryptAndVerify(authenticationCookie.getValue());
        if (serialisedAuthentication == null) {
            response.addCookie(createExpireAuthenticationCookie(request));
            return SecurityContextHolder.createEmptyContext();
        }
        Authentication authentication = authenticationSerializer.deserialize(serialisedAuthentication);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    /**
     * Stores the security context on completion of a request.
     *
     * @param securityContext the non-null context which was obtained from the holder.
     * @param request the current request
     * @param response the current response
     */
    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        SaveToCookieResponseWrapper responseWrapper = (SaveToCookieResponseWrapper) response;
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(securityContext);
        }
    }

    /**
     * Allows the repository to be queried as to whether it contains a security context for the
     * current request.
     *
     * @param request the current request
     * @return true if a context is found for the request, false otherwise
     */
    @Override
    public boolean containsContext(HttpServletRequest request) {
        return getAuthenticationCookie(request) != null;
    }

    public void setAuthenticationSerializer(AuthenticationSerializer authenticationSerializer) {
        this.authenticationSerializer = authenticationSerializer;
    }

    public void setAuthenticationCookieName(String authenticationCookieName) {
        this.authenticationCookieName = authenticationCookieName;
    }

    public void setAuthenticationCookiePath(String authenticationCookiePath) {
        this.authenticationCookiePath = authenticationCookiePath;
    }

    public void setAuthenticationCookieMaxAgeSeconds(int authenticationCookieMaxAgeSeconds) {
        this.authenticationCookieMaxAgeSeconds = authenticationCookieMaxAgeSeconds;
    }

    private Cookie getAuthenticationCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(authenticationCookieName)).findFirst();
        return maybeCookie.isPresent() ? maybeCookie.get() : null;
    }

    private Cookie createExpireAuthenticationCookie(HttpServletRequest request) {
        Cookie removeSessionCookie = new Cookie(authenticationCookieName, "");
        removeSessionCookie.setPath(authenticationCookiePath);
        removeSessionCookie.setMaxAge(0);
        removeSessionCookie.setHttpOnly(true);
        removeSessionCookie.setSecure(request.isSecure());
        return removeSessionCookie;
    }

    private class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;

        SaveToCookieResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
            super(response, true);
            this.request = request;
        }

        @Override
        protected void saveContext(SecurityContext securityContext) {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            Authentication authentication = securityContext.getAuthentication();
            if (authentication == null) {
                response.addCookie(createExpireAuthenticationCookie(request));
                return;
            }
            String serializedAuthentication = authenticationSerializer.serialize(authentication);
            String jwtToken = tokenEncryption.encryptAndSign(serializedAuthentication);
            response.addCookie(createAuthenticationCookie(jwtToken));
        }

        private Cookie createAuthenticationCookie(String cookieValue) {
            Cookie authenticationCookie = new Cookie(authenticationCookieName, cookieValue);
            authenticationCookie.setPath(authenticationCookiePath);
            authenticationCookie.setHttpOnly(true);
            authenticationCookie.setSecure(request.isSecure());
            authenticationCookie.setMaxAge(authenticationCookieMaxAgeSeconds);
            return authenticationCookie;
        }
    }
}
