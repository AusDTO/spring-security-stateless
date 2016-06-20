package au.gov.dto.springframework.security.web.savedrequest;

import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SimpleSavedRequest implements SavedRequest {
    private final String redirectUrl;

    public SimpleSavedRequest(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public List<Cookie> getCookies() {
        return Collections.emptyList();
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public List<String> getHeaderValues(String name) {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Collections.emptyList();
    }

    @Override
    public List<Locale> getLocales() {
        return Collections.emptyList();
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.emptyMap();
    }
}
