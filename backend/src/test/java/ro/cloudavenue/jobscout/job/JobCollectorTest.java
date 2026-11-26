package ro.cloudavenue.jobscout.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobCollectorTest {

    @Mock
    private JobService jobService;

    @Test
    void collect_shouldSubmitAllPostings_fromEveryFeed() {
        // Given
        JobFeed feed = mock(JobFeed.class);
        when(feed.source()).thenReturn(JobSource.ATS);
        when(feed.fetch()).thenReturn(List.of(
                new JobPosting("Java Developer Intern", "https://example.com/1"),
                new JobPosting("Junior Java Developer", "https://example.com/2")));
        when(jobService.submit(any(), any(), any())).thenReturn(Optional.empty());

        JobCollector collector = new JobCollector(List.of(feed), jobService);

        // When
        collector.collect();

        // Then
        verify(jobService).submit("Java Developer Intern", "https://example.com/1", JobSource.ATS);
        verify(jobService).submit("Junior Java Developer", "https://example.com/2", JobSource.ATS);
    }

    @Test
    void collect_shouldContinueWithNextFeed_whenFeedFails() {
        // Given
        JobFeed failingFeed = mock(JobFeed.class);
        when(failingFeed.fetch()).thenThrow(new IllegalStateException("IMAP unavailable"));

        JobFeed workingFeed = mock(JobFeed.class);
        when(workingFeed.source()).thenReturn(JobSource.ATS);
        when(workingFeed.fetch()).thenReturn(List.of(new JobPosting("Java Developer Intern", null)));
        when(jobService.submit(any(), any(), any())).thenReturn(Optional.empty());

        JobCollector collector = new JobCollector(List.of(failingFeed, workingFeed), jobService);

        // When
        collector.collect();

        // Then
        verify(jobService, times(1)).submit("Java Developer Intern", null, JobSource.ATS);
    }

    @Test
    void collect_shouldContinueWithNextPosting_whenSubmitFails() {
        // Given
        JobFeed feed = mock(JobFeed.class);
        when(feed.source()).thenReturn(JobSource.ATS);
        when(feed.fetch()).thenReturn(List.of(
                new JobPosting("First", null),
                new JobPosting("Second", null)));
        when(jobService.submit("First", null, JobSource.ATS)).thenThrow(new IllegalStateException("DB error"));
        when(jobService.submit("Second", null, JobSource.ATS)).thenReturn(Optional.empty());

        JobCollector collector = new JobCollector(List.of(feed), jobService);

        // When
        collector.collect();

        // Then
        verify(jobService).submit("Second", null, JobSource.ATS);
    }

    @Test
    void collect_shouldDoNothing_whenThereAreNoFeeds() {
        // Given
        JobCollector collector = new JobCollector(List.of(), jobService);

        // When
        collector.collect();

        // Then
        verify(jobService, never()).submit(any(), any(), any());
    }
}