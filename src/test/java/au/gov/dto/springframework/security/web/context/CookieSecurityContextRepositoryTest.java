package au.gov.dto.springframework.security.web.context;

import au.gov.dto.springframework.security.web.csrf.CookieCsrfTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CookieSecurityContextRepositoryTest {
    private static final String CSRF_COOKIE_NAME = "_csrf";
    private static final String SESSION_COOKIE_NAME = "session";
    public static final int CSRF_COOKIE_MAX_AGE_SECONDS = 3600;
    public static final int SESSION_COOKIE_MAX_AGE_SECONDS = 3600;

    @Test
    public void containsContextReturnsTrueIfSessionCookieExists() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(SESSION_COOKIE_NAME, ""));

        assertTrue(repository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void containsContextReturnsFalseIfSessionCookieDoesNotExist() throws Exception {
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
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        JwtUserDetailsSerializer jwtUserDetailsSerializer = createJwtUserDetailsSerializer();
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        String payload = jwtUserDetailsSerializer.serialize(userDetails);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, payload));
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
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        SecurityContext securityContext = repository.loadContext(requestResponseHolder);

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void expireSessionCookieForExpiredAuthToken() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        repository.loadContext(requestResponseHolder);

        ServletResponseWrapper responseWrapper = (ServletResponseWrapper) requestResponseHolder.getResponse();
        MockHttpServletResponse wrappedResponse = (MockHttpServletResponse) responseWrapper.getResponse();
        Cookie sessionCookie = wrappedResponse.getCookie(SESSION_COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(0));
        assertThat(sessionCookie.getValue(), isEmptyString());
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    @Test
    public void addSessionCookieOnResponseForNonEmptySecurityContext() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList()));
        String payload = createJwtUserDetailsSerializer().serialize(userDetails);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, payload));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie sessionCookie = response.getCookie(SESSION_COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(SESSION_COOKIE_MAX_AGE_SECONDS));
        assertThat(sessionCookie.getValue().length(), greaterThan(0));
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    @Test
    public void addCsrfCookieOnResponseForNonEmptySecurityContext() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList()));
        String payload = createJwtUserDetailsSerializer().serialize(userDetails);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, payload));
        request.setCookies(new Cookie(CSRF_COOKIE_NAME, "csrfTokenValue"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie csrfCookie = response.getCookie(CSRF_COOKIE_NAME);
        assertThat(csrfCookie.getMaxAge(), equalTo(CSRF_COOKIE_MAX_AGE_SECONDS));
        assertThat(csrfCookie.getValue(), equalTo("csrfTokenValue"));
        assertTrue(csrfCookie.getSecure());
        assertTrue(csrfCookie.isHttpOnly());
    }

    @Test
    public void addCsrfCookieOnResponseOnUserLogin() throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
        CookieSecurityContextRepository securityContextRepository = createCookieSecurityContextRepository(csrfTokenRepository);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = new User("username", "password", Collections.emptyList());
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList()));
        String payload = createJwtUserDetailsSerializer().serialize(userDetails);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SESSION_COOKIE_NAME, payload));
        MockHttpServletResponse response = new MockHttpServletResponse();
        CsrfToken token = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(token, request, response);
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        securityContextRepository.loadContext(requestResponseHolder);

        securityContextRepository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie[] cookies = Arrays.stream(response.getCookies()).filter(cookie -> cookie.getName().equals(CSRF_COOKIE_NAME)).toArray(Cookie[]::new);
        assertThat(cookies.length, equalTo(1));
        Cookie csrfCookie = cookies[0];
        assertThat(csrfCookie.getMaxAge(), equalTo(CSRF_COOKIE_MAX_AGE_SECONDS));
        assertThat(csrfCookie.getValue(), equalTo(token.getToken()));
        assertTrue(csrfCookie.getSecure());
        assertTrue(csrfCookie.isHttpOnly());
    }

    @Test
    public void expireSessionCookieForEmptySecurityContext() throws Exception {
        CookieSecurityContextRepository repository = createCookieSecurityContextRepository();
        SecurityContext emptySecurityContext = SecurityContextHolder.createEmptyContext();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(emptySecurityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie sessionCookie = response.getCookie(SESSION_COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(0));
        assertThat(sessionCookie.getValue(), isEmptyString());
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    private JwtUserDetailsSerializer createJwtUserDetailsSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        objectMapper.registerModule(module);
        return new JwtUserDetailsSerializer(objectMapper, Base64.getEncoder().encodeToString(new byte[32]));
    }

    private CookieSecurityContextRepository createCookieSecurityContextRepository() {
        CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
        csrfTokenRepository.setCsrfCookieName(CSRF_COOKIE_NAME);
        csrfTokenRepository.setCsrfCookieMaxAgeSeconds(CSRF_COOKIE_MAX_AGE_SECONDS);
        return createCookieSecurityContextRepository(csrfTokenRepository);
    }

    private CookieSecurityContextRepository createCookieSecurityContextRepository(CookieCsrfTokenRepository csrfTokenRepository) {
        SecurityContextSerializer securityContextSerializer = new SecurityContextSerializer(createJwtUserDetailsSerializer());
        securityContextSerializer.setSessionCookieName(SESSION_COOKIE_NAME);
        securityContextSerializer.setSessionCookieMaxAgeSeconds(SESSION_COOKIE_MAX_AGE_SECONDS);
        return new CookieSecurityContextRepository(
                csrfTokenRepository,
                securityContextSerializer,
                User.class);
    }
}
