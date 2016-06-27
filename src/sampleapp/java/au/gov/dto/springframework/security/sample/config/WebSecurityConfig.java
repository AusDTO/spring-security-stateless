package au.gov.dto.springframework.security.sample.config;

import au.gov.dto.springframework.security.web.authentication.CookieSavedRequestAwareAuthenticationSuccessHandler;
import au.gov.dto.springframework.security.web.authentication.StatelessSimpleUrlAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    private RequestCache requestCache;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .securityContext()
                .securityContextRepository(securityContextRepository)
                .and()
            .csrf()
                .csrfTokenRepository(csrfTokenRepository)
                .and()
            .requestCache()
                .requestCache(requestCache)
                .and()
            .anonymous()
                .disable()
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .successHandler(new CookieSavedRequestAwareAuthenticationSuccessHandler(requestCache))
                .failureHandler(new StatelessSimpleUrlAuthenticationFailureHandler())
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/");
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}
