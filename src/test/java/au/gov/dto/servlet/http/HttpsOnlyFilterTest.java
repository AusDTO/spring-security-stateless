package au.gov.dto.servlet.http;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class HttpsOnlyFilterTest {
    @Test
    public void testGetRedirectUrlReplacesScheme() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("any", "/path/morepath.extension?a=b&c=d");
        request.setScheme("http");
        request.setServerName("somehost.com");
        request.setServerPort(81);

        String redirectUrl = new HttpsOnlyFilter().getRedirectUrl(request);

        assertThat(redirectUrl, equalTo("https://somehost.com/path/morepath.extension?a=b&c=d"));
    }

    @Test
    public void testRedirectIssuedIfXForwardedProtoHeaderIsHttp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("any", "/path/morepath.extension?a=b&c=d");
        request.setScheme("http");
        request.setServerName("somehost.com");
        request.setServerPort(81);
        request.addHeader("X-Forwarded-Proto", "http");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new HttpsOnlyFilter().doFilter(request, response, filterChain);

        assertThat(response.getRedirectedUrl(), equalTo("https://somehost.com/path/morepath.extension?a=b&c=d"));
        assertThat(filterChain.getRequest(), nullValue());
        assertThat(filterChain.getResponse(), nullValue());
    }

    @Test
    public void testRedirectNotIssuedIfXForwardedProtoHeaderIsHttps() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("any", "/path");
        request.setScheme("https");
        request.addHeader("X-Forwarded-Proto", "https");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new HttpsOnlyFilter().doFilter(request, response, filterChain);

        assertThat(response.getRedirectedUrl(), nullValue());
        assertThat(filterChain.getRequest(), equalTo(request));
        assertThat(filterChain.getResponse(), equalTo(response));
    }

    @Test
    public void testRedirectNotIssuedIfXForwardedProtoHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("any", "/path");
        request.setScheme("http");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new HttpsOnlyFilter().doFilter(request, response, filterChain);

        assertThat(response.getRedirectedUrl(), nullValue());
        assertThat(filterChain.getRequest(), equalTo(request));
        assertThat(filterChain.getResponse(), equalTo(response));
    }
}
