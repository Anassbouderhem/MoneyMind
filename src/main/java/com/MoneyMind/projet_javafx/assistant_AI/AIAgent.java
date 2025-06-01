package com.MoneyMind.projet_javafx.assistant_AI;


import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class AIAgent {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();

    public String getAdvice(String userInput) throws IOException {
        JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", userInput);

        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new org.json.JSONArray().put(message))
                .put("temperature", 0.7);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            System.out.println("Code HTTP: " + statusCode);
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                System.err.println("Erreur API OpenAI: " + errorBody);
                return "Erreur API OpenAI, code HTTP: " + statusCode;
            }
            String responseBody = response.body().string();
            System.out.println("Réponse brute OpenAI: " + responseBody);

            JSONObject responseJson = new JSONObject(responseBody);
            return responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }

}
