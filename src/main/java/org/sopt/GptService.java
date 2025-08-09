package org.sopt;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GptService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api-key}")
    private String openAiApiKey;

    private final ObjectMapper om = new ObjectMapper();

    public String chat(String userPrompt) {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OPENAI_API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", openAiApiKey);

            Map<String, Object> body = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", "kb 금융 챗봇."),
                            Map.of("role", "user", "content", userPrompt)
                    )
            );

            post.setEntity(new StringEntity(om.writeValueAsString(body), StandardCharsets.UTF_8));

            try (CloseableHttpResponse res = http.execute(post)) {
                String resBody = EntityUtils.toString(res.getEntity(), StandardCharsets.UTF_8);
                JsonNode root = om.readTree(resBody);
                String content = root.at("/choices/0/message/content").asText(null);
                if (content == null) {
                    String err = root.path("error").path("message").asText("Unknown error");
                    return "OpenAI 호출 실패: " + err;
                }
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "OpenAI 호출 중 예외 발생: " + e.getMessage();
        }
    }
}
