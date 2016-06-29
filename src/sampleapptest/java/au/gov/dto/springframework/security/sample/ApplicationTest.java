package au.gov.dto.springframework.security.sample;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import static au.gov.dto.springframework.security.web.context.CookieSecurityContextRepository.DEFAULT_AUTHENTICATION_COOKIE_NAME;
import static au.gov.dto.springframework.security.web.csrf.CookieCsrfTokenRepository.DEFAULT_CSRF_COOKIE_NAME;
import static au.gov.dto.springframework.security.web.savedrequest.CookieRequestCache.DEFAULT_SAVEDREQUEST_COOKIE_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
    private final Log logger = LogFactory.getLog(this.getClass());

    private TestApplication application;
    private String base;
    private URL baseUrl;
    private WebClient webClient;

    @Before
    public void setup() throws Exception {
        application = new TestApplication().start();
        base = "http://localhost:" + application.getPort();
        baseUrl = new URL(base);
        webClient = new WebClient();
    }

    @After
    public void teardown() throws Exception {
        try {
            webClient.close();
        } catch (Exception e) {
            // ignore
        }
        application.stop();
    }

    @Test
    public void test() throws Exception {
        HtmlPage unauthenticatedPage =  webClient.getPage(base + "/");
        logCurrentPage();
        assertThat(unauthenticatedPage.getTitleText(), equalTo("Unauthenticated page"));
        assertCookiesPresent(false, false, false);
        HtmlPage loginPage = unauthenticatedPage.getAnchorByName("authenticatedPageLink").click();

        logCurrentPage();
        assertThat(loginPage.getTitleText(), equalTo("Login Page"));
        assertCookiesPresent(false, true, true);
        assertThat(getSavedRequestUrl(), equalTo(base + "/authenticated"));
        String preLoginCsrfToken = getCsrfToken();

        HtmlForm loginForm = loginPage.getFormByName("f");
        loginForm.getInputByName("username").setValueAttribute("user");
        loginForm.getInputByName("password").setValueAttribute("password");
        HtmlPage authenticatedPage = loginForm.getInputByName("submit").click();

        logCurrentPage();
        assertThat(authenticatedPage.getTitleText(), equalTo("Authenticated page"));
        assertCookiesPresent(true, false, true);
        String postLoginCsrfToken = getCsrfToken();
        assertThat(preLoginCsrfToken, not(equalTo(postLoginCsrfToken)));

        HtmlForm logoutForm = authenticatedPage.getFormByName("logoutForm");
        HtmlPage pageAfterLogout = logoutForm.getInputByName("logout").click();

        logCurrentPage();
        assertThat(pageAfterLogout.getTitleText(), equalTo("Unauthenticated page"));
        assertCookiesPresent(false, false, false);
    }

    private void assertCookiesPresent(boolean authenticationCookie, boolean savedRequestCookie, boolean csrfCookie) {
        assertThat("Cookies: " + getCookies().toString(), hasAuthenticationCookie(), equalTo(authenticationCookie));
        assertThat("Cookies: " + getCookies().toString(), hasSavedRequestCookie(), equalTo(savedRequestCookie));
        assertThat("Cookies: " + getCookies().toString(), hasCsrfCookie(), equalTo(csrfCookie));
    }

    private boolean hasAuthenticationCookie() {
        return getCookies().stream().anyMatch(cookie -> DEFAULT_AUTHENTICATION_COOKIE_NAME.equals(cookie.getName()));
    }

    private boolean hasCsrfCookie() {
        return getCookies().stream().anyMatch(cookie -> DEFAULT_CSRF_COOKIE_NAME.equals(cookie.getName()));
    }

    private boolean hasSavedRequestCookie() {
        return getCookies().stream().anyMatch(cookie -> DEFAULT_SAVEDREQUEST_COOKIE_NAME.equals(cookie.getName()));
    }

    private String getCsrfToken() {
        return getCookies().stream().filter(cookie -> DEFAULT_CSRF_COOKIE_NAME.equals(cookie.getName())).findFirst().get().getValue();
    }

    private String getSavedRequestUrl() {
        String savedRequestCookieValue = getCookies().stream().filter(cookie -> DEFAULT_SAVEDREQUEST_COOKIE_NAME.equals(cookie.getName())).findFirst().get().getValue();
        return new String(Base64.getMimeDecoder().decode(savedRequestCookieValue), StandardCharsets.ISO_8859_1);
    }

    private Set<Cookie> getCookies() {
        return webClient.getCookies(baseUrl);
    }

    private void logCurrentPage() {
        HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        logger.info("On page title=[" + page.getTitleText() + "] url=[" + page.getUrl().toString() + "]");
    }
}

class TestApplication {
    private AnnotationConfigEmbeddedWebApplicationContext context;
    private int port;

    TestApplication start() {
        return start(0);
    }

    TestApplication start(int port) {
        context = (AnnotationConfigEmbeddedWebApplicationContext) SpringApplication.run(Application.class, "--port=" + port);
        this.port = context.getEmbeddedServletContainer().getPort();
        return this;
    }

    TestApplication stop() {
        SpringApplication.exit(context);
        return this;
    }

    int getPort() {
        return port;
    }
}
