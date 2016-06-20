package org.springframework.security.web.savedrequest;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is required because
 * {@link org.springframework.security.web.savedrequest.SavedRequestAwareWrapper} is package-private.
 */
public class PublicSavedRequestAwareWrapper extends SavedRequestAwareWrapper {
    public PublicSavedRequestAwareWrapper(SavedRequest saved, HttpServletRequest request) {
        super(saved, request);
    }
}
