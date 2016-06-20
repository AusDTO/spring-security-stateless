package au.gov.dto.springframework.security.web.context;

import com.nimbusds.jwt.JWTClaimsSet;

public interface JwtClaimsSetVerifier {
    boolean verify(JWTClaimsSet claimsSet);
}
