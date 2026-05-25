package com.tub.p1_autenticacao.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

public class JwtUtil {

    private static final String SECRET_KEY = "tub_super_secret_key_for_jwt_generation_2026_dai_project";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String generateToken(Long id, String email, String cargo, long expirationMillis) {
        try {
            // Header
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String headerJson = objectMapper.writeValueAsString(header);
            String headerEncoded = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

            // Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("email", email);
            payload.put("cargo", cargo.toUpperCase());
            payload.put("exp", System.currentTimeMillis() + expirationMillis);
            String payloadJson = objectMapper.writeValueAsString(payload);
            String payloadEncoded = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Signature
            String data = headerEncoded + "." + payloadEncoded;
            String signature = calculateHmacSha256(data, SECRET_KEY);

            return data + "." + signature;
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

            String headerEncoded = parts[0];
            String payloadEncoded = parts[1];
            String signatureProvided = parts[2];

            // Verify signature
            String data = headerEncoded + "." + payloadEncoded;
            String signatureCalculated = calculateHmacSha256(data, SECRET_KEY);
            if (!signatureCalculated.equals(signatureProvided)) {
                return null; // Invalid signature
            }

            // Parse payload
            byte[] payloadBytes = base64UrlDecode(payloadEncoded);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, Map.class);

            // Check expiration
            Long exp = ((Number) payload.get("exp")).longValue();
            if (System.currentTimeMillis() > exp) {
                return null; // Expired token
            }

            return payload;
        } catch (Exception e) {
            return null; // Parse or verification error
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }

    private static String calculateHmacSha256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hmacBytes);
    }
}
