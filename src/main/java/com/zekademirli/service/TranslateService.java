package com.zekademirli.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TranslateService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public TranslateService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
        this.objectMapper = new ObjectMapper();
    }

    public String getAnswer(String word) {
        String promptGemini = "Provide the etymology of the word \"" + word + "\" as follows:\n" +
                "1. Write one EXACTLY concise sentence in English explaining the word's origin and etymology.\n" +
                "2. Translate that exact same etymology explanation into Azerbaijani language (not just describing the word in Azerbaijani, but the actual translation of the English etymology).\n" +
                "The format should be exactly like this example:\n" +
                "English: The word \"example\" comes from the Latin \"exemplum\" meaning \"sample\".\n\n" +
                "Azərbaycan: \"Example\" sözü \"nümunə\" mənasını verən Latın \"exemplum\" sözündən gəlir.\n\n" +
                "The empty line between the English and Azerbaijani parts is required.";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", promptGemini)))
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
            JsonNode rootNode = objectMapper.readTree(response);

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
                    return formatResponseBilingual(word, fullText);
                }
            }

            return "Could not find text content in the response";
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    private String formatResponseBilingual(String word, String text) {
        String cleaned = text.replaceAll("\\*", "");

        Pattern englishPattern = Pattern.compile("English:\\s*(.*?)(?=\\s*Azərbaycan:|$)", Pattern.DOTALL);
        Pattern azerbaijaniPattern = Pattern.compile("Azərbaycan:\\s*(.*?)$", Pattern.DOTALL);

        Matcher englishMatcher = englishPattern.matcher(cleaned);
        Matcher azerbaijaniMatcher = azerbaijaniPattern.matcher(cleaned);

        String englishPart = englishMatcher.find() ? englishMatcher.group(1).trim() : "";
        String azerbaijaniPart = azerbaijaniMatcher.find() ? azerbaijaniMatcher.group(1).trim() : "";

        if (englishPart.isEmpty() || azerbaijaniPart.isEmpty()) {
            String[] paragraphs = cleaned.split("\\n\\n|\\n");
            if (paragraphs.length >= 2) {
                englishPart = paragraphs[0].trim();
                azerbaijaniPart = paragraphs[1].trim();
            }
        }

        StringBuilder result = new StringBuilder();

        result.append("English: ")
                .append(englishPart);

        result.append("\n\n");

        result.append("Azərbaycan: ")
                .append(azerbaijaniPart);

        return result.toString();
    }
}