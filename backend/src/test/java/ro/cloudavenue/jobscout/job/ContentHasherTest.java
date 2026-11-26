package ro.cloudavenue.jobscout.job;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentHasherTest {

    @Test
    void hash_shouldBeSame_whenOnlyCaseAndWhitespaceDiffer() {
        // Given
        String original = "  Java  Developer\n\tBucharest ";
        String normalized = "java developer bucharest";

        // When
        String first = ContentHasher.hash(original);
        String second = ContentHasher.hash(normalized);

        // Then
        assertEquals(first, second);
    }

    @Test
    void hash_shouldDiffer_whenTextDiffers() {
        // When
        String java = ContentHasher.hash("Java Developer");
        String go = ContentHasher.hash("Go Developer");

        // Then
        assertNotEquals(java, go);
    }

    @Test
    void hash_shouldReturn64HexCharacters() {
        // When
        String hash = ContentHasher.hash("Java Developer");

        // Then
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }
}