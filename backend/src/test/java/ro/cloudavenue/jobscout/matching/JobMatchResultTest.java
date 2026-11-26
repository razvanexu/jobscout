package ro.cloudavenue.jobscout.matching;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class JobMatchResultTest {
    @ParameterizedTest
    @ValueSource(ints = {0, 50, 100})
    void jobMatchResult_shouldAcceptValidScore(int score) {
        //Given
        String reason = "Test_Reason";
        //When
        JobMatchResult result = new JobMatchResult(score, reason);

        //Then
        assertEquals(score, result.score());
        assertEquals(reason, result.reason());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50, 101, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void jobMatchResult_shouldThrowIllegalArgumentException_onInvalidScore(int score) {
        //Given
        String reason = "Test_Reason";
        //When //Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new JobMatchResult(score, reason));
        assertEquals("Score must be 0 -> 100, got: " + score, ex.getMessage());
    }
}
