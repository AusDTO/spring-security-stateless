package au.gov.dto.springframework.security.web.context;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;

import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CookieSecurityContextRepositoryTest {
    private static final String AUTHENTICATION_COOKIE_NAME = "authentication";
    private static final int AUTHENTICATION_COOKIE_MAX_AGE_SECONDS = 3600;

    @Test
    public void containsContextReturnsTrueIfAuthenticationCookieExists() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(AUTHENTICATION_COOKIE_NAME, ""));

        assertTrue(repository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void containsContextReturnsFalseIfAuthenticationCookieDoesNotExist() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        assertFalse(repository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void returnsEmptySecurityContextForUnauthenticatedRequest() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContext securityContext = repository.loadContext(new HttpRequestResponseHolder(request, response));

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void returnsSecurityContextWithAuthenticationForAuthenticatedRequest() throws Exception {
        TokenEncryption tokenEncryption = createJwtEncryption();
        AuthenticationSerializer authenticationSerializer = new JsonAuthenticationSerializer();
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository(tokenEncryption, authenticationSerializer);
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
        String serializedAuthentication = authenticationSerializer.serialize(authentication);
        String payload = tokenEncryption.encryptAndSign(serializedAuthentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(AUTHENTICATION_COOKIE_NAME, payload));
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContext securityContext = repository.loadContext(new HttpRequestResponseHolder(request, response));

        assertThat(securityContext.getAuthentication(), notNullValue());
        UserDetails authenticatedUserDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        assertThat(authenticatedUserDetails.getUsername(), equalTo(userDetails.getUsername()));
    }

    @Test
    public void returnsEmptySecurityContextForExpiredAuthToken() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(AUTHENTICATION_COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        SecurityContext securityContext = repository.loadContext(requestResponseHolder);

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void expireAuthenticationCookieForExpiredAuthToken() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(AUTHENTICATION_COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        repository.loadContext(requestResponseHolder);

        ServletResponseWrapper responseWrapper = (ServletResponseWrapper) requestResponseHolder.getResponse();
        MockHttpServletResponse wrappedResponse = (MockHttpServletResponse) responseWrapper.getResponse();
        Cookie authenticationCookie = wrappedResponse.getCookie(AUTHENTICATION_COOKIE_NAME);
        assertThat(authenticationCookie.getMaxAge(), equalTo(0));
        assertThat(authenticationCookie.getValue(), isEmptyString());
        assertTrue(authenticationCookie.getSecure());
        assertTrue(authenticationCookie.isHttpOnly());
    }

    @Test
    public void addAuthenticationCookieOnResponseForNonEmptySecurityContext() throws Exception {
        TokenEncryption tokenEncryption = createJwtEncryption();
        AuthenticationSerializer authenticationSerializer = new JsonAuthenticationSerializer();
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository(tokenEncryption, authenticationSerializer);
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        String serializedAuthentication = authenticationSerializer.serialize(authentication);
        String payload = tokenEncryption.encryptAndSign(serializedAuthentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(AUTHENTICATION_COOKIE_NAME, payload));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie authenticationCookie = response.getCookie(AUTHENTICATION_COOKIE_NAME);
        assertThat(authenticationCookie.getMaxAge(), equalTo(AUTHENTICATION_COOKIE_MAX_AGE_SECONDS));
        assertThat(authenticationCookie.getValue().length(), greaterThan(0));
        assertTrue(authenticationCookie.getSecure());
        assertTrue(authenticationCookie.isHttpOnly());
    }

    @Test
    public void expireAuthenticationCookieForEmptySecurityContext() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        SecurityContext emptySecurityContext = SecurityContextHolder.createEmptyContext();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(emptySecurityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie authenticationCookie = response.getCookie(AUTHENTICATION_COOKIE_NAME);
        assertThat(authenticationCookie.getMaxAge(), equalTo(0));
        assertThat(authenticationCookie.getValue(), isEmptyString());
        assertTrue(authenticationCookie.getSecure());
        assertTrue(authenticationCookie.isHttpOnly());
    }

    private JwtEncryption createJwtEncryption() {
        return new JwtEncryption("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
    }

    private CookieSecurityContextRepository createCookieSecurityContextRepository() {
        return createCookieSecurityContextRepository(createJwtEncryption(), new JsonAuthenticationSerializer());
    }

    private CookieSecurityContextRepository createCookieSecurityContextRepository(TokenEncryption tokenEncryption, AuthenticationSerializer authenticationSerializer) {
        CookieSecurityContextRepository securityContextRepository = new CookieSecurityContextRepository(tokenEncryption);
        securityContextRepository.setAuthenticationSerializer(authenticationSerializer);
        securityContextRepository.setAuthenticationCookieName(AUTHENTICATION_COOKIE_NAME);
        securityContextRepository.setAuthenticationCookieMaxAgeSeconds(AUTHENTICATION_COOKIE_MAX_AGE_SECONDS);
        return securityContextRepository;
    }
}
