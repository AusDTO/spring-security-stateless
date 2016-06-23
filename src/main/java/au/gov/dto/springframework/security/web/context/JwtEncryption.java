package au.gov.dto.springframework.security.web.context;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class JwtEncryption implements TokenEncryption {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final byte[] sessionJwtEncryptionKey;

    private List<JwtClaimsSetVerifier> jwtClaimsSetVerifiers = new ArrayList<>(Collections.singletonList(new ExpirationJwtClaimsSetVerifier()));
    private int jwtExpirationSeconds = 3600;
    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
    private EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;

    public JwtEncryption(String sessionJwtEncryptionKeyBase64) {
        Assert.notNull(sessionJwtEncryptionKeyBase64);
        this.sessionJwtEncryptionKey = Base64.getDecoder().decode(sessionJwtEncryptionKeyBase64);
    }

    @Override
    public String encryptAndSign(String jwtSubject) {
        try {
            Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(jwtExpirationSeconds).toInstant());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(jwtSubject).expirationTime(date).build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
            signedJWT.sign(new MACSigner(sessionJwtEncryptionKey));
            JWEHeader jweHeader = new JWEHeader.Builder(jweAlgorithm, encryptionMethod).contentType("JWT").build();
            JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
            jweObject.encrypt(new DirectEncrypter(sessionJwtEncryptionKey));
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Could not create JWT", e);
        }
    }

    @Override
    public String decryptAndVerify(String encryptedAndSignedJwt) {
        try {
            JWEObject jweObject = JWEObject.parse(encryptedAndSignedJwt);
            jweObject.decrypt(new DirectDecrypter(sessionJwtEncryptionKey));
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
            if (!signedJWT.verify(new MACVerifier(sessionJwtEncryptionKey))) {
                logger.warn("JWT signature verification failed.");
                return null;
            }
            for (JwtClaimsSetVerifier verifier : jwtClaimsSetVerifiers) {
                if (!verifier.verify(signedJWT.getJWTClaimsSet())) {
                    logger.warn("JWT claims verification failed.");
                    return null;
                }
            }
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Could not parse JWT", e);
        }
    }

    public void setJwtClaimsSetVerifiers(List<JwtClaimsSetVerifier> jwtClaimsSetVerifiers) {
        Assert.notNull(jwtClaimsSetVerifiers);
        this.jwtClaimsSetVerifiers = new ArrayList<>(jwtClaimsSetVerifiers);
    }

    public boolean addJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        Assert.notNull(jwtClaimsSetVerifier);
        return this.jwtClaimsSetVerifiers.add(jwtClaimsSetVerifier);
    }

    public void setJwtExpirationSeconds(int jwtExpirationSeconds) {
        this.jwtExpirationSeconds = jwtExpirationSeconds;
    }

    public void setJwsAlgorithm(JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(jwsAlgorithm);
        this.jwsAlgorithm = jwsAlgorithm;
    }

    public void setJweAlgorithm(JWEAlgorithm jweAlgorithm) {
        Assert.notNull(jweAlgorithm);
        this.jweAlgorithm = jweAlgorithm;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        Assert.notNull(encryptionMethod);
        this.encryptionMethod = encryptionMethod;
    }
}
