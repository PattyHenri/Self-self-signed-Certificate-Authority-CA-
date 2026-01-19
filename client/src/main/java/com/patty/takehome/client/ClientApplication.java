package com.patty.takehome.client;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        Map<String, String> a = parseArgs(args);

        // 1) URL: MUST match your server endpoint mapping.
        // In your server, open PatchController.java and confirm the path:
        // e.g. @PatchMapping("/patch") => https://localhost:8443/patch
        String url = a.getOrDefault("url", "https://localhost:8443/patch");

        // 2) Keystore containing CLIENT cert + private key (PKCS12)
        // Put file under: client/src/main/resources/tls/client.p12
        String keystorePath = a.getOrDefault("keystore", "classpath:tls/client.p12");
        char[] keystorePass = a.getOrDefault("keystorePass", "changeit").toCharArray();

        // 3) Truststore containing CA cert that signed the SERVER cert (PKCS12)
        // Put file under: client/src/main/resources/tls/truststore.p12
        String truststorePath = a.getOrDefault("truststore", "classpath:tls/truststore.p12");
        char[] truststorePass = a.getOrDefault("truststorePass", "changeit").toCharArray();

        int timeoutSeconds = Integer.parseInt(a.getOrDefault("timeoutSeconds", "20"));

        SSLContext sslContext = buildMtlsSslContext(keystorePath, keystorePass, truststorePath, truststorePass);

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                // Empty PATCH body:
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        System.out.println("Sending mTLS PATCH (empty body) to: " + url);

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status: " + resp.statusCode());
        System.out.println("Response Body:\n" + resp.body());
    }

    private static SSLContext buildMtlsSslContext(
            String keystorePath, char[] keystorePass,
            String truststorePath, char[] truststorePass
    ) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = openPath(keystorePath)) {
            keyStore.load(in, keystorePass);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePass);

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = openPath(truststorePath)) {
            trustStore.load(in, truststorePass);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return ssl;
    }

    private static InputStream openPath(String path) throws Exception {
        if (path.startsWith("classpath:")) {
            String cp = path.substring("classpath:".length());
            InputStream in = ClientApplication.class.getClassLoader().getResourceAsStream(cp);
            if (in == null) throw new IllegalArgumentException("Classpath resource not found: " + cp);
            return in;
        }
        return new FileInputStream(path);
    }

    private static Map<String, String> parseArgs(String[] args) {
        // Supports: --key=value
        Map<String, String> map = new HashMap<>();
        for (String s : args) {
            if (s.startsWith("--") && s.contains("=")) {
                int i = s.indexOf('=');
                map.put(s.substring(2, i), s.substring(i + 1));
            }
        }
        return map;
    }
}
