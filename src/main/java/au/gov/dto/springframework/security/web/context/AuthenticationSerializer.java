package au.gov.dto.springframework.security.web.context;

import org.springframework.security.core.Authentication;

public interface AuthenticationSerializer {
    String serialize(Authentication authentication);

    Authentication deserialize(String serialisedAuthentication);
}
