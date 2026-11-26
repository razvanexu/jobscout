package ro.cloudavenue.jobscout.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class OllamaJobMatcher implements JobMatcher {

    private static final Map<String, Object> FORMAT = Map.of(
            "type", "object",
            "properties", Map.of(
                    "score", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                    "reason", Map.of("type", "string", "maxLength", 300)
            ),
            "required", List.of("score", "reason")
    );

    private static final String PROMPT_TEMPLATE = """
        You are screening job ads for the candidate described below.
        Use the fit levels in the profile to choose the score range from the job's main technology.
        Then adjust the score within that range for role level, required experience and location.
        Write the reason in at most 2 short sentences, under 40 words. Do not repeat the profile or the job ad.

        Candidate profile:
        %s

        Job ad:
        %s
        """;

    private final RestClient restClient;
    private final String model;
    private final JsonMapper jsonMapper;
    private final String profile;

    public OllamaJobMatcher(RestClient.Builder builder,
                            @Value("${ollama.base-url}") String baseUrl,
                            @Value("${ollama.model}") String model, JsonMapper jsonMapper,
                            @Value("${jobscout.profile-path}") Resource profileResource) throws IOException {
        this.jsonMapper = jsonMapper;
        this.restClient = builder.baseUrl(baseUrl).build();
        this.model = model;
        this.profile = profileResource.getContentAsString(StandardCharsets.UTF_8);
    }

    @Override
    public JobMatchResult match(String jobDescription) {
        String prompt = PROMPT_TEMPLATE.formatted(profile, jobDescription);
        ChatRequest request = new ChatRequest(model, false, List.of(new Message("user", prompt)), FORMAT);
        ChatResponse response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.message() == null) {
            throw new IllegalStateException("Ollama returned an empty response");
        }
        String content = response.message().content;

        return jsonMapper.readValue(content, JobMatchResult.class);
    }

    record Message(String role, String content) {

    }

    record ChatRequest(String model, boolean stream, List<Message> messages, Map<String, Object> format) {

    }

    record ChatResponse(Message message) {

    }
}
