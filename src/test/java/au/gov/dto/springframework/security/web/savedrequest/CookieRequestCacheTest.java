package au.gov.dto.springframework.security.web.savedrequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CookieRequestCacheTest {
    private static final String COOKIE_NAME = "savedrequest";

    @Test
    public void saveRequestSetsSessionCookieOnResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(false);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setServerPort(8080);
        request.setRequestURI("/some/path/name");
        request.setQueryString("query=value&a=b");
        MockHttpServletResponse response = new MockHttpServletResponse();

        createCookieRequestCache().saveRequest(request, response);

        Cookie cookie = response.getCookie(COOKIE_NAME);
        assertThat(cookie, notNullValue());
        assertThat(cookie.getPath(), nullValue());
        assertThat(cookie.getMaxAge(), equalTo(-1));
        assertThat(cookie.isHttpOnly(), equalTo(true));
        String savedUrl = new String(Base64.getMimeDecoder().decode(cookie.getValue()), StandardCharsets.ISO_8859_1);
        assertThat(savedUrl, equalTo("http://example.com:8080/some/path/name?query=value&a=b"));
    }

    @Test
    public void saveRequestSetsSessionCookieOnResponseWithHttpsSchemeIfRequestSecure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setServerPort(443);
        request.setRequestURI("/some/path/name");
        request.setQueryString("query=value&a=b");
        MockHttpServletResponse response = new MockHttpServletResponse();

        createCookieRequestCache().saveRequest(request, response);

        Cookie cookie = response.getCookie(COOKIE_NAME);
        String savedUrl = new String(Base64.getMimeDecoder().decode(cookie.getValue()), StandardCharsets.ISO_8859_1);
        assertThat(savedUrl, equalTo("https://example.com/some/path/name?query=value&a=b"));
    }

    @Test
    public void getRequestReturnsNullIfCookiesOnRequestAreNull() throws Exception {
        SavedRequest savedRequest = createCookieRequestCache().getRequest(new MockHttpServletRequest(), new MockHttpServletResponse());
        assertThat(savedRequest, nullValue());
    }

    @Test
    public void getRequestReturnsNullIfCookiesOnRequestDoNotContainSavedRequestCookie() throws Exception {
        CookieRequestCache requestCache = createCookieRequestCache();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie[1]);

        SavedRequest savedRequest = requestCache.getRequest(request, new MockHttpServletResponse());

        assertThat(savedRequest, nullValue());
    }

    @Test
    public void getRequestReturnsSavedIfSavedRequestCookieExistsOnRequest() throws Exception {
        CookieRequestCache requestCache = createCookieRequestCache();
        MockHttpServletRequest request = new MockHttpServletRequest();
        String savedUrl = "https://example.com/some/path/name?query=value&a=b";
        String encodedUrl = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\n'}).encodeToString(savedUrl.getBytes(StandardCharsets.ISO_8859_1));
        request.setCookies(new Cookie(COOKIE_NAME, encodedUrl));

        SavedRequest savedRequest = requestCache.getRequest(request, new MockHttpServletResponse());

        assertThat(savedRequest, notNullValue());
        assertThat(savedRequest.getRedirectUrl(), equalTo(savedUrl));
        assertThat(savedRequest.getCookies().size(), equalTo(0));
        assertThat(savedRequest.getMethod(), equalToIgnoringCase("GET"));
        assertThat(savedRequest.getHeaderValues("anything").size(), equalTo(0));
        assertThat(savedRequest.getHeaderNames().size(), equalTo(0));
        assertThat(savedRequest.getLocales().size(), equalTo(0));
        assertThat(savedRequest.getParameterValues("anything").length, equalTo(0));
        assertThat(savedRequest.getParameterMap().size(), equalTo(0));
    }

    @Test
    public void removeRequestSetsExpiredCookieOnResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        createCookieRequestCache().removeRequest(new MockHttpServletRequest(), response);

        Cookie expiredSavedRequestCookie = response.getCookie(COOKIE_NAME);
        assertThat(expiredSavedRequestCookie.getValue(), equalTo(""));
        assertThat(expiredSavedRequestCookie.getMaxAge(), equalTo(0));
    }

    @Test
    public void getMatchingRequestReturnsNullAndDoesNotSetExpiredCookieOnResponseIfNoSavedRequestFoundInRequestCookie() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletRequest matchingRequest = createCookieRequestCache().getMatchingRequest(new MockHttpServletRequest(), response);

        assertThat(matchingRequest, nullValue());
        assertThat(response.getCookie(COOKIE_NAME), nullValue());
    }

    @Test
    public void getMatchingRequestReturnsNullAndSetsExpiredCookieOnResponseIfSavedRequestFoundInRequestCookie() throws Exception {
        CookieRequestCache requestCache = createCookieRequestCache();
        MockHttpServletRequest request = new MockHttpServletRequest();
        String savedUrl = "https://example.com/some/path/name?query=value&a=b";
        String encodedUrl = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\n'}).encodeToString(savedUrl.getBytes(StandardCharsets.ISO_8859_1));
        request.setCookies(new Cookie(COOKIE_NAME, encodedUrl));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletRequest matchingRequest = requestCache.getMatchingRequest(request, response);

        assertThat(matchingRequest, nullValue());
        Cookie expiredSavedRequestCookie = response.getCookie(COOKIE_NAME);
        assertThat(expiredSavedRequestCookie.getValue(), equalTo(""));
        assertThat(expiredSavedRequestCookie.getMaxAge(), equalTo(0));
    }

    private CookieRequestCache createCookieRequestCache() {
        CookieRequestCache cookieRequestCache = new CookieRequestCache();
        cookieRequestCache.setSavedRequestCookieName(COOKIE_NAME);
        return cookieRequestCache;
    }
}
