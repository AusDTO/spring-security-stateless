package au.gov.dto.springframework.security.sample.config;

import au.gov.dto.servlet.http.HttpSessionCreatedListener;
import au.gov.dto.servlet.http.HttpsOnlyFilter;
import au.gov.dto.servlet.http.NoHttpSessionFilter;
import au.gov.dto.springframework.security.web.context.CookieSecurityContextRepository;
import au.gov.dto.springframework.security.web.context.JwtEncryption;
import au.gov.dto.springframework.security.web.csrf.CookieCsrfTokenRepository;
import au.gov.dto.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSessionListener;
import java.util.Collections;

@Configuration
class AppConfig {
    @Bean
    ServletContextInitializer noSessionTrackingServletContextInitializer() {
        return servletContext -> servletContext.setSessionTrackingModes(Collections.emptySet());
    }

    @Bean
    ServletListenerRegistrationBean<HttpSessionListener> httpSessionCreatedListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegistrationBean = new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setListener(new HttpSessionCreatedListener());
        return listenerRegistrationBean;
    }

    @Bean
    FilterRegistrationBean noHttpSessionFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new NoHttpSessionFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    CookieCsrfTokenRepository csrfTokenRepository() {
        return new CookieCsrfTokenRepository();
    }

    @Bean
    @Autowired
    CookieSecurityContextRepository securityContextRepository(@Value("${session.encryption.key.base64}") String sessionEncryptionKeyBase64) {
        return new CookieSecurityContextRepository(new JwtEncryption(sessionEncryptionKeyBase64));
    }

    @Bean
    CookieRequestCache cookieRequestCache() {
        return new CookieRequestCache();
    }

    @Bean
    FilterRegistrationBean httpsOnlyFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpsOnlyFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}
