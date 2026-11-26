package ro.cloudavenue.jobscout.job;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.cloudavenue.jobscout.matching.JobMatchResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
class JobControllerTest {

    private static final String DESCRIPTION = "Java Developer Intern";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Test
    void submit_shouldReturnCreated_whenJobIsNew() throws Exception {
        // Given
        Job job = new Job("hash", JobSource.MANUAL, null, DESCRIPTION);
        job.applyMatch(new JobMatchResult(85, "Good fit"));
        when(jobService.submit(DESCRIPTION, null, JobSource.MANUAL)).thenReturn(Optional.of(job));

        // When / Then
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Java Developer Intern"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("MANUAL"))
                .andExpect(jsonPath("$.score").value(85))
                .andExpect(jsonPath("$.reason").value("Good fit"));
    }

    @Test
    void submit_shouldReturnConflict_whenJobIsDuplicate() throws Exception {
        // Given
        when(jobService.submit(DESCRIPTION, null, JobSource.MANUAL)).thenReturn(Optional.empty());

        // When / Then
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Java Developer Intern"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void submit_shouldReturnBadRequest_whenDescriptionIsBlank() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"   "}
                                """))
                .andExpect(status().isBadRequest());

        verify(jobService, never()).submit(any(), any(), any());
    }
}