package ro.cloudavenue.jobscout.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Component
public class OllamaJobMatcher implements JobMatcher {

    private static final Map<String, Object> FORMAT = Map.of(
            "type", "object",
            "properties", Map.of(
                    "score", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                    "reason", Map.of("type", "string")
            ),
            "required", List.of("score", "reason")
    );

    private static final String PROMPT_TEMPLATE = """
            Rate from 0 to 100 how well this job fits a junior Java backend developer.
            Explain the score in 2-3 sentences.
            
            Job:
            %s
            """;

    private final RestClient restClient;
    private final String model;
    private final JsonMapper jsonMapper;

    public OllamaJobMatcher(RestClient.Builder builder,
                            @Value("${ollama.base-url}") String baseUrl,
                            @Value("${ollama.model}") String model, JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.restClient = builder.baseUrl(baseUrl).build();
        this.model = model;
    }

    @Override
    public JobMatchResult match(String jobDescription) {
        String prompt = PROMPT_TEMPLATE.formatted(jobDescription);
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
