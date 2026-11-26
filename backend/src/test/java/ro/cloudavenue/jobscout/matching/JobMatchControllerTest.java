package ro.cloudavenue.jobscout.matching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobMatchController.class)
class JobMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobMatcher jobMatcher;

    @Test
    void match_shouldReturnResult_whenRequestIsValid() throws Exception {
        when(jobMatcher.match("Junior Java Developer"))
                .thenReturn(new JobMatchResult(85, "Good fit"));

        mockMvc.perform(post("/api/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jobDescription":"Junior Java Developer"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85))
                .andExpect(jsonPath("$.reason").value("Good fit"));
    }
}
