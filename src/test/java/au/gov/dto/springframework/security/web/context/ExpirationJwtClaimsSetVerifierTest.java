package au.gov.dto.springframework.security.web.context;

import au.gov.dto.springframework.security.web.context.ExpirationJwtClaimsSetVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpirationJwtClaimsSetVerifierTest {
    @Test
    public void shouldReturnFalseIfExpiryDateIsNotSet() throws Exception {
        ExpirationJwtClaimsSetVerifier expirationJwtClaimsSetVerifier = new ExpirationJwtClaimsSetVerifier();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().build();
        assertFalse(expirationJwtClaimsSetVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnTrueForFutureExpiryDate() throws Exception {
        ExpirationJwtClaimsSetVerifier expirationJwtClaimsSetVerifier = new ExpirationJwtClaimsSetVerifier();
        Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(30).toInstant());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertTrue(expirationJwtClaimsSetVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnFalseForPastExpiryDate() throws Exception {
        ExpirationJwtClaimsSetVerifier expirationJwtClaimsSetVerifier = new ExpirationJwtClaimsSetVerifier();
        Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(30).toInstant());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertFalse(expirationJwtClaimsSetVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnFalseForExpiryDateBefore01Jan1970() throws Exception {
        ExpirationJwtClaimsSetVerifier expirationJwtClaimsSetVerifier = new ExpirationJwtClaimsSetVerifier();
        Date date = Date.from(Instant.EPOCH.minusSeconds(60 * 60 * 24));
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertFalse(expirationJwtClaimsSetVerifier.verify(jwtClaimsSet));
    }
}
