package au.gov.dto.springframework.security.web.csrf;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Cross-site request forgery (CSRF or CSRF) protection using double submit cookies:
 * https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29_Prevention_Cheat_Sheet#Double_Submit_Cookies
 * <p>
 * Issues to be aware of when using double submit cookies for CSRF protection: http://security.stackexchange.com/a/61039
 * <p>
 * Some code borrowed from Pivotal Cloud Foundry under the Apache 2.0 license:
 * https://github.com/cloudfoundry/uaa/blob/41dba9d81dbdf24ede4fb9719de28b1b88b3e1b4/common/src/main/java/org/cloudfoundry/identity/uaa/web/CookieBasedCsrfTokenRepository.java
 */
public class CookieCsrfTokenRepository implements CsrfTokenRepository {
    private SecureRandom secureRandom = new SecureRandom();
    private String csrfHeaderName = "X-CSRF-TOKEN";
    private String csrfCookieName = "_csrf";
    private String csrfCookiePath = null;
    private int csrfCookieMaxAgeSeconds = 3600;

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String tokenValue = new BigInteger(130, secureRandom).toString(32); // http://stackoverflow.com/a/41156
        return new DefaultCsrfToken(csrfHeaderName, csrfCookieName, tokenValue);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        Cookie csrfCookie;
        if (token == null) {
            csrfCookie = new Cookie(csrfCookieName, "");
            csrfCookie.setMaxAge(0);
        } else {
            csrfCookie = new Cookie(token.getParameterName(), token.getToken());
            csrfCookie.setMaxAge(csrfCookieMaxAgeSeconds);
        }
        csrfCookie.setHttpOnly(true);
        csrfCookie.setSecure(request.isSecure());
        csrfCookie.setPath(csrfCookiePath);
        response.addCookie(csrfCookie);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie != null && csrfCookieName.equals(cookie.getName())) {
                    return new DefaultCsrfToken(csrfHeaderName, csrfCookieName, cookie.getValue());
                }
            }
        }
        return null;
    }

    public void setSecureRandom(SecureRandom secureRandom) {
        Assert.notNull(secureRandom);
        this.secureRandom = secureRandom;
    }

    public void setCsrfHeaderName(String csrfHeaderName) {
        Assert.notNull(csrfHeaderName);
        this.csrfHeaderName = csrfHeaderName;
    }

    public void setCsrfCookieName(String csrfCookieName) {
        Assert.notNull(csrfCookieName);
        this.csrfCookieName = csrfCookieName;
    }

    public void setCsrfCookiePath(String csrfCookiePath) {
        this.csrfCookiePath = csrfCookiePath;
    }

    public void setCsrfCookieMaxAgeSeconds(int csrfCookieMaxAgeSeconds) {
        this.csrfCookieMaxAgeSeconds = csrfCookieMaxAgeSeconds;
    }
}
