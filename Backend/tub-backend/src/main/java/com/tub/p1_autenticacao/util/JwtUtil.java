package com.tub.p1_autenticacao.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "TUB_SUPER_SECRET_KEY_FOR_JWT_HMAC_SHA256_TUB_PROJECT_2026";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String generateToken(Long id, String email, String cargo, long expMillis) {
        try {
            // Header
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String headerJson = objectMapper.writeValueAsString(header);
            String headerBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

            // Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("email", email);
            payload.put("cargo", cargo);
            payload.put("exp", System.currentTimeMillis() + expMillis);
            String payloadJson = objectMapper.writeValueAsString(payload);
            String payloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Signature
            String signInput = headerBase64 + "." + payloadBase64;
            String signature = hmacSha256(signInput, SECRET);

            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public static Map<String, Object> parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String headerBase64 = parts[0];
            String payloadBase64 = parts[1];
            String signature = parts[2];

            // Verify signature
            String signInput = headerBase64 + "." + payloadBase64;
            String expectedSignature = hmacSha256(signInput, SECRET);
            if (!expectedSignature.equals(signature)) {
                return null;
            }

            // Decode payload
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payloadBase64);
            String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);

            // Check expiration
            Long exp = ((Number) claims.get("exp")).longValue();
            if (exp < System.currentTimeMillis()) {
                return null; // Expired
            }

            return claims;
        } catch (Exception e) {
            return null; // Invalid token
        }
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
    }
}
