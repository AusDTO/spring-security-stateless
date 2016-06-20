package au.gov.dto.springframework.security.web.context;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class ExpirationJwtClaimsSetVerifier implements JwtClaimsSetVerifier {
    private final Log logger = LogFactory.getLog(this.getClass());

    private int maxClockSkewSeconds = 60;

    @Override
    public boolean verify(JWTClaimsSet claimsSet) {
        Date now = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expirationTime = claimsSet.getExpirationTime();
        if (expirationTime == null) {
            logger.warn("Missing expiration date in JWT claims set");
            return false;
        }
        return DateUtils.isAfter(expirationTime, now, maxClockSkewSeconds);
    }

    public void setMaxClockSkewSeconds(int maxClockSkewSeconds) {
        this.maxClockSkewSeconds = maxClockSkewSeconds;
    }
}
