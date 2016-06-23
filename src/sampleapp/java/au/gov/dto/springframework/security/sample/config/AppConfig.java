package au.gov.dto.springframework.security.sample.config;

import au.gov.dto.servlet.NoHttpSessionFilter;
import au.gov.dto.servlet.http.HttpSessionCreatedListener;
import au.gov.dto.springframework.security.web.context.CookieSecurityContextRepository;
import au.gov.dto.springframework.security.web.context.JwtEncryption;
import au.gov.dto.springframework.security.web.context.JwtUserDetailsSerializer;
import au.gov.dto.springframework.security.web.context.TokenEncryption;
import au.gov.dto.springframework.security.web.csrf.CookieCsrfTokenRepository;
import au.gov.dto.springframework.security.web.savedrequest.CookieRequestCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpSessionListener;
import java.util.Collections;

@Configuration
class AppConfig {
    @Autowired
    @Value("${session.encryption.key.base64}")
    private String sessionEncryptionKeyBase64;

    @Bean
    ServletContextInitializer noSessionTrackingServletContextInitializer() {
        return servletContext -> servletContext.setSessionTrackingModes(Collections.emptySet());
    }

    @Bean
    FilterRegistrationBean noHttpSessionFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new NoHttpSessionFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    ServletListenerRegistrationBean<HttpSessionListener> httpSessionCreatedListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegistrationBean = new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setListener(new HttpSessionCreatedListener());
        return listenerRegistrationBean;
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        return new CookieCsrfTokenRepository();
    }

    @Bean
    @Autowired
    public CookieSecurityContextRepository securityContextRepository(CsrfTokenRepository csrfTokenRepository, ObjectMapper objectMapper) {
        return new CookieSecurityContextRepository(new JwtEncryption(sessionEncryptionKeyBase64));
    }

    @Bean
    public CookieRequestCache cookieRequestCache() {
        return new CookieRequestCache();
    }
}
