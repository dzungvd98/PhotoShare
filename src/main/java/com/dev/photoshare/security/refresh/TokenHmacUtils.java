package com.dev.photoshare.security.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TokenHmacUtils {

    private final ThreadLocal<Mac> macTL;

    public TokenHmacUtils(
            @Value("${security.refresh-token.secret}") String secret
    ) {
        this.macTL = ThreadLocal.withInitial(() -> {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                ));
                return mac;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public String hmac(String token) {
        return Base64.getEncoder().encodeToString(
                macTL.get().doFinal(token.getBytes(StandardCharsets.UTF_8))
        );
    }
}
