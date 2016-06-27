package au.gov.dto.servlet.http;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Force HTTPS in environments that support it.
 *
 * Inspects the X-Forwarded-Proto header to decide. This header is set by load balancers to inform
 * the proxied application of the protocol used by the client request.
 */
public class HttpsOnlyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String forwardedProtocolHeader = request.getHeader("x-forwarded-proto");
        if ("http".equalsIgnoreCase(forwardedProtocolHeader)) {
            String redirectUrl = getRedirectUrl(request);
            ((HttpServletResponse) servletResponse).sendRedirect(redirectUrl);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    String getRedirectUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        try {
            URI uri = new URI(requestUrl);
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(uri.getHost())
                    .path(uri.getPath())
                    .query(uri.getQuery())
                    .build();
            return uriComponents.toUriString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not parse URL [" + requestUrl + "]", e);
        }
    }
}
