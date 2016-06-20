package au.gov.dto.springframework.security.web.context;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class SecurityContextSerializer {
    private final JwtUserDetailsSerializer jwtUserDetailsSerializer;

    private String sessionCookieName = "session";
    private String sessionCookiePath = null;
    private int sessionCookieMaxAgeSeconds = 3600;

    public SecurityContextSerializer(JwtUserDetailsSerializer jwtUserDetailsSerializer) {
        this.jwtUserDetailsSerializer = jwtUserDetailsSerializer;
    }

    public SecurityContext deserialize(HttpServletRequest request, HttpServletResponse response, Class<? extends UserDetails> userDetailsType) {
        UserDetails userDetails = getUserFromSessionCookie(request, userDetailsType);
        if (userDetails == null) {
            response.addCookie(createLogoutCookie(request));
            return SecurityContextHolder.createEmptyContext();
        }
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(userDetails);
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    public void serialize(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        if (securityContext.getAuthentication() == null) {
            response.addCookie(createLogoutCookie(request));
            return;
        }
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        String jwtToken = jwtUserDetailsSerializer.serialize(userDetails);
        Cookie sessionCookie = new Cookie(sessionCookieName, jwtToken);
        sessionCookie.setPath(sessionCookiePath);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        sessionCookie.setMaxAge(sessionCookieMaxAgeSeconds);
        response.addCookie(sessionCookie);
    }

    public boolean hasSessionCookie(HttpServletRequest request) {
        return getSessionCookie(request) != null;
    }

    public void setSessionCookieName(String sessionCookieName) {
        Assert.notNull(sessionCookieName);
        this.sessionCookieName = sessionCookieName;
    }

    public void setSessionCookiePath(String sessionCookiePath) {
        Assert.notNull(sessionCookiePath);
        this.sessionCookiePath = sessionCookiePath;
    }

    public void setSessionCookieMaxAgeSeconds(int sessionCookieMaxAgeSeconds) {
        this.sessionCookieMaxAgeSeconds = sessionCookieMaxAgeSeconds;
    }

    protected <T extends UserDetails> T getUserFromSessionCookie(HttpServletRequest request, Class<T> userDetailsType) {
        Cookie sessionCookie = getSessionCookie(request);
        return sessionCookie == null ? null : jwtUserDetailsSerializer.deserialize(sessionCookie.getValue(), userDetailsType);
    }

    protected Cookie getSessionCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(sessionCookieName)).findFirst();
        return maybeCookie.isPresent() ? maybeCookie.get() : null;
    }

    protected Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
    }

    protected Cookie createLogoutCookie(HttpServletRequest request) {
        Cookie removeSessionCookie = new Cookie(sessionCookieName, "");
        removeSessionCookie.setPath(sessionCookiePath);
        removeSessionCookie.setMaxAge(0);
        removeSessionCookie.setHttpOnly(true);
        removeSessionCookie.setSecure(request.isSecure());
        return removeSessionCookie;
    }
}
