package au.gov.dto.springframework.security.web.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JwtUserDetailsSerializer {
    private final byte[] sessionJwtEncryptionKey;
    private final ObjectMapper objectMapper;

    private List<JwtClaimsSetVerifier> jwtClaimsSetVerifiers = new ArrayList<>();
    private int jwtExpirationSeconds = 3600;

    public JwtUserDetailsSerializer(ObjectMapper objectMapper, String sessionJwtEncryptionKeyBase64) {
        this.objectMapper = objectMapper;
        this.sessionJwtEncryptionKey = Base64.getDecoder().decode(sessionJwtEncryptionKeyBase64);
        this.jwtClaimsSetVerifiers.add(new ExpirationJwtClaimsSetVerifier());
    }

    public String serialize(UserDetails userDetails) {
        try {
            Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(jwtExpirationSeconds).toInstant());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(objectMapper.writeValueAsString(userDetails)).expirationTime(date).build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(new MACSigner(sessionJwtEncryptionKey));
            JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).contentType("JWT").build();
            JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
            jweObject.encrypt(new DirectEncrypter(sessionJwtEncryptionKey));
            return jweObject.serialize();
        } catch (JsonProcessingException | JOSEException e) {
            throw new RuntimeException("Could not serialize UserDetails to JWT", e);
        }
    }

    public <T extends UserDetails> T deserialize(String jweString, Class<T> userDetailsType) {
        try {
            JWEObject jweObject = JWEObject.parse(jweString);
            jweObject.decrypt(new DirectDecrypter(sessionJwtEncryptionKey));
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
            if (!signedJWT.verify(new MACVerifier(sessionJwtEncryptionKey))) {
                return null;
            }
            for (JwtClaimsSetVerifier verifier : jwtClaimsSetVerifiers) {
                if (!verifier.verify(signedJWT.getJWTClaimsSet())) {
                    return null;
                }
            }
            return objectMapper.readValue(signedJWT.getJWTClaimsSet().getSubject(), userDetailsType);
        } catch (ParseException | JOSEException | IOException e) {
            throw new RuntimeException("Could not deserialize JWT to User", e);
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
}
