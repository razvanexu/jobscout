package ro.cloudavenue.jobscout.matching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import tools.jackson.core.JacksonException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(OllamaJobMatcher.class)
@TestPropertySource(properties = "jobscout.profile-path=classpath:test-profile.md")
class OllamaJobMatcherTest {

    private static final String VALID_RESPONSE = """
            {"message":{"role":"assistant","content":"{\\"score\\":85,\\"reason\\":\\"Good fit\\"}"}}
            """;

    private static final String OUT_OF_RANGE_RESPONSE = """
        {"message":{"role":"assistant","content":"{\\"score\\":150,\\"reason\\":\\"Too high\\"}"}}
        """;

    @Autowired
    private OllamaJobMatcher matcher;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void match_shouldReturnResult_whenResponseIsValid() {
        server.expect(requestTo("http://localhost:11434/api/chat"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.model").value("qwen3:4b-instruct"))
                .andExpect(jsonPath("$.stream").value(false))
                .andExpect(jsonPath("$.messages[0].role").value("user"))
                .andExpect(jsonPath("$.messages[0].content").value(containsString("TEST PROFILE")))
                .andRespond(withSuccess(VALID_RESPONSE, MediaType.APPLICATION_JSON));
    }

    @Test
    void match_shouldThrow_whenResponseIsEmpty() {
        // Given
        server.expect(requestTo("http://localhost:11434/api/chat"))
                .andRespond(withSuccess());

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> matcher.match("Junior Java Developer"));

        server.verify();
    }

    @Test
    void match_shouldThrow_whenScoreIsOutOfRange() {
        // Given
        server.expect(requestTo("http://localhost:11434/api/chat"))
                .andRespond(withSuccess(OUT_OF_RANGE_RESPONSE, MediaType.APPLICATION_JSON));

        // When / Then
        assertThrows(JacksonException.class,
                () -> matcher.match("Junior Java Developer"));

        server.verify();
    }


}