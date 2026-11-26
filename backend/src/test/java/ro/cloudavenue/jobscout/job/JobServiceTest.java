package ro.cloudavenue.jobscout.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.cloudavenue.jobscout.matching.JobMatchResult;
import ro.cloudavenue.jobscout.matching.JobMatcher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    private static final String DESCRIPTION = "Java Developer Intern";

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMatcher jobMatcher;

    @InjectMocks
    private JobService jobService;

    @Test
    void submit_shouldReturnEmpty_whenJobIsDuplicate() {
        // Given
        when(jobRepository.existsByContentHash(anyString())).thenReturn(true);

        // When
        Optional<Job> result = jobService.submit(DESCRIPTION, null, JobSource.MANUAL);

        // Then
        assertTrue(result.isEmpty());
        verify(jobRepository, never()).save(any());
        verify(jobMatcher, never()).match(anyString());
    }

    @Test
    void submit_shouldSaveAndMatch_whenJobIsNew() {
        // Given
        when(jobRepository.existsByContentHash(anyString())).thenReturn(false);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMatcher.match(DESCRIPTION)).thenReturn(new JobMatchResult(85, "Good fit"));

        // When
        Optional<Job> result = jobService.submit(DESCRIPTION, null, JobSource.MANUAL);

        // Then
        assertTrue(result.isPresent());
        assertEquals(85, result.get().getScore());
        assertEquals("Good fit", result.get().getReason());
        verify(jobRepository, times(2)).save(any(Job.class));
    }

    @Test
    void submit_shouldKeepJobWithoutScore_whenMatchingFails() {
        // Given
        when(jobRepository.existsByContentHash(anyString())).thenReturn(false);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMatcher.match(DESCRIPTION)).thenThrow(new IllegalStateException("Ollama unavailable"));

        // When
        Optional<Job> result = jobService.submit(DESCRIPTION, null, JobSource.MANUAL);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getScore());
        verify(jobRepository, times(1)).save(any(Job.class));
    }
}