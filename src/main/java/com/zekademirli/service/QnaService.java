package com.zekademirli.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QnaService {

    //Access to Api key and url [Gemini]
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public QnaService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }


    public String test() {
        return new Random().nextInt(100) + "test";
    }

//    public String getAnswer(String question) {
//
//        //request body here: {"contents": [{"parts":[{"text": "Simple greeting"}] }]}
//
//        //construct the request payload
//        Map<String, Object> parts = Collections.singletonMap("text", question);
//        Map<String, Object> contents = Collections.singletonMap("parts", parts);
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("contents", contents);
//
//        //Make Api call
//
//
//        String response = webClient.post()
//                .uri(geminiApiUrl + geminiApiKey)
//                .header("Content-Type", "application/json")
//                .header(HttpHeaders.AUTHORIZATION, geminiApiKey)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
////        .header(HttpHeaders.AUTHORIZATION, geminiApiKey)
////        .contentType(MediaType.APPLICATION_JSON)
//
//        //return response
//        return response;
//    }


    public String getAnswer(String word) {
        String woc="i want to learn word"+ word  +"i want you to give me words in azerbaijani that pronounced like" + word+ " not lexically connected but only pronounced alike\n" +
                "now i want you to use one of these words and exact translation of "+word+" itself in a azerbaijani sentence";
        /*
         "defer"  "defer" not lexically connected but only pronounced alike
         */

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

        return extractText(response);
    }

    private String extractText(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return "No response received.";
        }

        // Regex pattern to extract the text inside "text": "..."
        Pattern pattern = Pattern.compile("\"text\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonResponse);

        StringBuilder extractedText = new StringBuilder();
        while (matcher.find()) {
            extractedText.append(matcher.group(1)).append("\n");
        }

        return extractedText.toString().trim().isEmpty() ? "No valid text found." : extractedText.toString().trim();
    }

}
