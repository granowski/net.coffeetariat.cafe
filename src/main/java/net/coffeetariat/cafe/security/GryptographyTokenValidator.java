package net.coffeetariat.cafe.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GryptographyTokenValidator {

    private final String publicKeyUrlPattern;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, PublicKey> keyCache = new ConcurrentHashMap<>();

    public GryptographyTokenValidator(
            @Value("${gryptography.public-key-url-pattern:http://localhost:8080/api/clients/%s/public-key}") String publicKeyUrlPattern,
            ObjectMapper objectMapper) {
        this.publicKeyUrlPattern = publicKeyUrlPattern;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public String validateAndGetClientId(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String header = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        JsonNode payloadNode = objectMapper.readTree(payload);
        String clientId = payloadNode.has("clientId") ? payloadNode.get("clientId").asText() : payloadNode.get("sub").asText();
        
        // Verify claims
        long exp = payloadNode.get("exp").asLong();
        if (System.currentTimeMillis() / 1000L > exp) {
            throw new IllegalStateException("Token expired");
        }
        
        if (!"grypto-auth".equals(payloadNode.get("iss").asText())) {
             throw new IllegalStateException("Invalid issuer");
        }

        PublicKey publicKey = getPublicKey(clientId);
        
        String signingInput = parts[0] + "." + parts[1];
        byte[] signature = Base64.getUrlDecoder().decode(parts[2]);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(signingInput.getBytes(StandardCharsets.UTF_8));

        if (!sig.verify(signature)) {
            throw new SecurityException("Invalid signature");
        }

        return clientId;
    }

    private PublicKey getPublicKey(String clientId) throws Exception {
        if (keyCache.containsKey(clientId)) {
            return keyCache.get(clientId);
        }

        String url = String.format(publicKeyUrlPattern, clientId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch public key for client: " + clientId + ", status: " + response.statusCode());
        }

        PublicKey publicKey = parsePemPublicKey(response.body());
        keyCache.put(clientId, publicKey);
        return publicKey;
    }

    private PublicKey parsePemPublicKey(String pem) throws Exception {
        String publicPem = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}
