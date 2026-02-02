package com.albrix.Backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.albrix.Backend.entity.Chat;
import com.albrix.Backend.entity.Message;

@Service
public class PromptBuilderService {

    private static final int MEMORY_LIMIT = 5;

    public String buildPrompt(Chat chat, List<Message> history, String userPrompt) {

        StringBuilder context = new StringBuilder();
        context.append("""
        You are a senior software engineer.

        Project Type: %s
        Language: %s
        Framework: %s
        Database: %s

        """.formatted(
                chat.getProjectType(),
                chat.getLanguage(),
                chat.getFramework(),
                chat.getDatabaseType()
        ));
        context.append("Conversation so far:\n");

        history.stream()
               .skip(Math.max(0, history.size() - MEMORY_LIMIT))
               .forEach(msg -> {
                   context.append(msg.getRole())
                          .append(": ")
                          .append(msg.getContent())
                          .append("\n");
               });
        context.append("""
        
        Current User Request:
        %s

        %s
        """.formatted(
                userPrompt,
                resolveOutputType(chat.getOutputType())
        ));

        return context.toString();
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