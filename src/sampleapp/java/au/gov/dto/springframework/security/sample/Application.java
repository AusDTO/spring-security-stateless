package au.gov.dto.springframework.security.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Cipher;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        int maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
        if (maxAllowedKeyLength < Integer.MAX_VALUE) {
            System.err.println("Max allowed key length is " + maxAllowedKeyLength + " bits." + System.getProperty("line.separator") +
                    "You appear to be using the limited strength Java Cryptography Extension (JCE) policy files." + System.getProperty("line.separator") +
                    "Please install the unlimited strength policy files, available here:" + System.getProperty("line.separator") +
                    "http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html");
            System.exit(1);
        }
        SpringApplication.run(Application.class, args);
    }
}
