package au.gov.dto.springframework.security.web.csrf;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Some code borrowed from Pivotal Cloud Foundry under the Apache 2.0 license:
 * https://github.com/cloudfoundry/uaa/blob/41dba9d81dbdf24ede4fb9719de28b1b88b3e1b4/common/src/test/java/org/cloudfoundry/identity/uaa/web/CookieBasedCsrfTokenRepositoryTests.java
 */
public class CookieCsrfTokenRepositoryTest {
    @Test
    public void testSaveAndLoadToken() throws Exception {
        CookieCsrfTokenRepository repo = new CookieCsrfTokenRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CsrfToken token = repo.generateToken(request);
        repo.saveToken(token, request, response);

        Cookie cookie = response.getCookie(token.getParameterName());
        assertNotNull(cookie);
        assertEquals(token.getToken(), cookie.getValue());
        assertEquals(true, cookie.isHttpOnly());

        request.setCookies(cookie);

        CsrfToken saved = repo.loadToken(request);
        assertEquals(token.getToken(), saved.getToken());
        assertEquals(token.getHeaderName(), saved.getHeaderName());
        assertEquals(token.getParameterName(), saved.getParameterName());
    }
}
