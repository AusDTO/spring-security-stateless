package au.gov.dto.springframework.security.web.authentication;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;

public class CookieSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public CookieSavedRequestAwareAuthenticationSuccessHandler(RequestCache requestCache) {
        setRequestCache(requestCache);
    }
}
