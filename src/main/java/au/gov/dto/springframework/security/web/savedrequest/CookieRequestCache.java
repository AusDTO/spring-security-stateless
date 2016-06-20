package au.gov.dto.springframework.security.web.savedrequest;

import org.springframework.security.web.savedrequest.PublicSavedRequestAwareWrapper;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class CookieRequestCache implements RequestCache {
    private final Base64.Encoder base64Encoder = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\n'});
    private final Base64.Decoder base64Decoder = Base64.getMimeDecoder();

    private String savedRequestCookieName = "_savedrequest";
    private String savedRequestCookiePath = null;
    private int savedRequestCookieMaxAgeSeconds = -1;  // default to session cookie (non-persistent)

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String requestUrl = request.getRequestURL().toString();
        try {
            URI requestUri = new URI(requestUrl);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                    .scheme(request.isSecure() ? "https" : "http")
                    .host(requestUri.getHost())
                    .path(requestUri.getPath())
                    .query(requestUri.getQuery())
                    .fragment(requestUri.getFragment());
            if ((request.isSecure() && requestUri.getPort() != 443) || (!request.isSecure() && requestUri.getPort() != 80)) {
                uriComponentsBuilder.port(requestUri.getPort());
            }
            String redirectUrl = uriComponentsBuilder.build().toUriString();
            String redirectUrlBase64 = base64Encoder.encodeToString(redirectUrl.getBytes(StandardCharsets.ISO_8859_1));
            Cookie savedRequestCookie = new Cookie(savedRequestCookieName, redirectUrlBase64);
            savedRequestCookie.setPath(savedRequestCookiePath);
            savedRequestCookie.setMaxAge(savedRequestCookieMaxAgeSeconds);
            savedRequestCookie.setSecure(request.isSecure());
            savedRequestCookie.setHttpOnly(true);
            response.addCookie(savedRequestCookie);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem creating URI from request.getRequestURL() = [" + requestUrl + "]", e);
        }
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(savedRequestCookieName)).findFirst();
        if (!maybeCookie.isPresent()) {
            return null;
        }
        Cookie savedRequestCookie = maybeCookie.get();
        String redirectUrl = new String(base64Decoder.decode(savedRequestCookie.getValue()), StandardCharsets.ISO_8859_1);
        return new SimpleSavedRequest(redirectUrl);
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = getRequest(request, response);
        if (savedRequest == null) {
            return null;
        }
        removeRequest(request, response);
        return new PublicSavedRequestAwareWrapper(savedRequest, request);
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        Cookie removeSavedRequestCookie = new Cookie(savedRequestCookieName, "");
        removeSavedRequestCookie.setPath(savedRequestCookiePath);
        removeSavedRequestCookie.setMaxAge(0);
        removeSavedRequestCookie.setSecure(request.isSecure());
        removeSavedRequestCookie.setHttpOnly(true);
        response.addCookie(removeSavedRequestCookie);
    }

    public void setSavedRequestCookieName(String savedRequestCookieName) {
        this.savedRequestCookieName = savedRequestCookieName;
    }

    public void setSavedRequestCookiePath(String savedRequestCookiePath) {
        this.savedRequestCookiePath = savedRequestCookiePath;
    }

    public void setSavedRequestCookieMaxAgeSeconds(int savedRequestCookieMaxAgeSeconds) {
        this.savedRequestCookieMaxAgeSeconds = savedRequestCookieMaxAgeSeconds;
    }
}
