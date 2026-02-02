package com.albrix.Backend.service;

import org.springframework.stereotype.Service;

import com.albrix.Backend.entity.Chat;

@Service
public class PromptBuilderService {

    public String buildPrompt(Chat chat, String userPrompt) {

        return """
        You are a senior software engineer.

        Project Type: %s
        Language: %s
        Framework: %s
        Database: %s
        Output Type: %s

        User Requirement:
        %s

        Generate clean, production-ready code with best practices.
        """
        .formatted(
            chat.getProjectType(),
            chat.getLanguage(),
            chat.getFramework(),
            chat.getDatabaseType(),
            chat.getOutputType(),
            userPrompt
        );
    }
}
