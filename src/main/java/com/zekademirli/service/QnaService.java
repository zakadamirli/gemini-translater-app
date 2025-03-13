package com.zekademirli.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class QnaService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public QnaService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
        this.objectMapper = new ObjectMapper();
    }

    public String test() {
        return new Random().nextInt(100) + "test";
    }

    public String getAnswer(String word) {
        String woc = "Find an Azerbaijani word that sounds similar to \"" + word + "\" (only phonetically similar, not lexically connected). " +
                "Then create an Azerbaijani sentence using both this phonetically similar word AND the Azerbaijani translation of \"" + word + "\" itself. " +
                "Format your response EXACTLY like this example:\n\n" +
                "\"" + word + "\" (mənası: [meaning of word]) sözünə səslənən Azərbaycan sözü [phonetically similar word].\n" +
                "[Example sentence in Azerbaijani using both the phonetically similar word and the translation of " + word + "].\n\n" +
                "Do not add any explanations or additional text. Do not use any asterisks (*) or markdown formatting in your response. ONLY provide these two lines in the exact format shown.";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", woc)))
                )
        );

        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(response);

            // Extract text from the specific path in the JSON structure
            if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && rootNode.get("candidates").size() > 0) {
                JsonNode textNode = rootNode
                        .get("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text");

                if (!textNode.isMissingNode()) {
                    String fullText = textNode.asText();

                    // Remove any remaining asterisks or markdown that might be in the response
                    fullText = fullText.replaceAll("\\*\\*", "");

                    return fullText;
                }
            }

            return "Could not find text content in the response";
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }
}