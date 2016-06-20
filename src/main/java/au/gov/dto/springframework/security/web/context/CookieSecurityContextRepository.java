package au.gov.dto.springframework.security.web.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/CookieSecurityContextRepository.java
 */
public class CookieSecurityContextRepository implements SecurityContextRepository {
    private final CsrfTokenRepository csrfTokenRepository;
    private final SecurityContextSerializer securityContextSerializer;
    private final Class<? extends UserDetails> userDetailsType;

    public CookieSecurityContextRepository(CsrfTokenRepository csrfTokenRepository,
                                           SecurityContextSerializer securityContextSerializer,
                                           Class<? extends UserDetails> userDetailsType) {
        this.csrfTokenRepository = csrfTokenRepository;
        this.securityContextSerializer = securityContextSerializer;
        this.userDetailsType = userDetailsType;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SaveToCookieResponseWrapper responseWrapper = new SaveToCookieResponseWrapper(requestResponseHolder.getRequest(), requestResponseHolder.getResponse());
        requestResponseHolder.setResponse(responseWrapper);
        return securityContextSerializer.deserialize(requestResponseHolder.getRequest(), requestResponseHolder.getResponse(), userDetailsType);
    }

    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        SaveToCookieResponseWrapper responseWrapper = (SaveToCookieResponseWrapper) response;
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(securityContext);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return securityContextSerializer.hasSessionCookie(request);
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
            if (securityContext.getAuthentication() != null) {
                CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
                if (csrfToken != null) {
                    csrfTokenRepository.saveToken(csrfToken, request, response);
                }
            }
            securityContextSerializer.serialize(securityContext, request, response);
        }
    }
}
