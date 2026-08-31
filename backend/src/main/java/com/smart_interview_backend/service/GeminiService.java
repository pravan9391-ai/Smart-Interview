package com.smart_interview_backend.service;

import com.google.genai.Client;
import com.google.genai.gaos.models.interactions.CreateModelInteraction;
import com.google.genai.gaos.models.interactions.InteractionsInput;
import com.google.genai.gaos.models.interactions.Model;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import com.google.genai.gaos.models.interactions.Content;
import com.google.genai.gaos.models.interactions.ModelOutputStep;
import com.google.genai.gaos.models.interactions.TextContent;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

private final Client client;

public GeminiService(
        @Value("${gemini.api.key}") String apiKey) {

this.client = Client.builder()
        .apiKey(apiKey)
        .build();
}

public String generateQuestions(String prompt) {

CreateModelInteraction request =
        CreateModelInteraction.builder()
                .model(Model.of("gemini-3.5-flash"))
                .input(InteractionsInput.of(prompt))
                .build();

var interaction = client.interactions
        .create(CreateInteractionRequestBody.of(request))
        .interaction()
        .get();

System.out.println("Gemini interaction status: "
        + interaction.status());

StringBuilder output = new StringBuilder();

interaction.steps().ifPresent(steps -> {

for (var step : steps) {

        if (step instanceof ModelOutputStep modelOutputStep) {

        modelOutputStep.content().ifPresent(contents -> {

                for (Content content : contents) {

                if (content instanceof TextContent textContent) {

                        textContent.text().ifPresent(text -> {
                        output.append(text);
                        });

                }
                }
        });
        }
}
});

String result = output.toString().trim();

System.out.println("========== GEMINI TEXT ==========");
System.out.println(result);
System.out.println("=================================");

return result;
}

@PreDestroy
public void close() {
client.close();
}
}