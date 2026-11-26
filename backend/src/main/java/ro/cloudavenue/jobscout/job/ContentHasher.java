package ro.cloudavenue.jobscout.job;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

final class ContentHasher {

    private ContentHasher(){}
    public static String hash(String text) {
        Objects.requireNonNull(text, "text must not be null");
        String normalized = text.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
        byte[] normBytes = normalized.getBytes(StandardCharsets.UTF_8);
        byte[] hash = digest.digest(normBytes);
        return HexFormat.of().formatHex(hash);
    }
}
