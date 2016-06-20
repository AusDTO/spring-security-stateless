package au.gov.dto.springframework.security.web.authentication;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class StatelessSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    public StatelessSimpleUrlAuthenticationFailureHandler() {
        super();
        setAllowSessionCreation(false);
    }

    public StatelessSimpleUrlAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
        setAllowSessionCreation(false);
    }

    @Override
    protected boolean isAllowSessionCreation() {
        return false;
    }

    @Override
    public void setAllowSessionCreation(boolean allowSessionCreation) {
        super.setAllowSessionCreation(false);
    }
}
