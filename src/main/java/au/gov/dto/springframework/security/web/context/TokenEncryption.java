package au.gov.dto.springframework.security.web.context;

public interface TokenEncryption {
    String encryptAndSign(String token);

    String decryptAndVerify(String encryptedToken);
}
