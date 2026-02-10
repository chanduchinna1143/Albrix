package com.albrix.Backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.albrix.Backend.entity.Chat;
import com.albrix.Backend.entity.Message;

@Service
public class PromptBuilderService {

    private static final int MEMORY_LIMIT = 5;

    public String buildPrompt(Chat chat, List<Message> history, String userPrompt) {

        // FIRST REAL PROMPT → BUILD PROJECT
        if (history.size() <= 1) {
            return """
            You are an expert software developer.

            The user wants to build an application with:
            - Project Type: %s
            - Language: %s
            - Framework: %s
            - Database: %s
            - Output Type: %s

            Build the application based on the user's request below.

            User request:
            %s
            """.formatted(
                chat.getProjectType(),
                chat.getLanguage(),
                chat.getFramework(),
                chat.getDatabaseType(),
                chat.getOutputType(),
                userPrompt
            );
        }

        // LATER PROMPTS → NORMAL CONVERSATION
        return """
        You are continuing an existing software project.

        Respond naturally to the user's message.
        - If the user is thanking or acknowledging, do not generate code.
        - If the user asks a question, explain.
        - If the user asks for changes, update the project.
        - Do NOT repeat full project structure unless asked.

        User message:
        %s
        """.formatted(userPrompt);
    }
    private String resolveOutputType(String outputType) {

        return switch (outputType) {

            case "CODE_ONLY" -> """
                Return ONLY clean, production-ready source code.
                Do NOT add explanation or markdown.
                """;

            case "EXPLANATION" -> """
                Explain the solution step-by-step in simple language.
                Do NOT include any source code.
                """;

            case "FOLDER_STRUCTURE" -> """
                Return ONLY the project folder structure in a tree format.
                Do NOT include source code.
                """;

            case "FULL_PROJECT" -> """
                First show the folder structure.
                Then provide complete source code.
                Finally explain the implementation step-by-step.
                """;

            default -> """
                Generate clean, production-ready code with best practices.
                """;
        };
    }
}