package com.example.cap.Service;

import com.example.cap.Model.ConstructionProject;
import com.example.cap.Model.Lands;
import com.example.cap.Repository.ConstructionProjectRepository;
import com.example.cap.Repository.LandsRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AIService {

    private final ChatClient.Builder chatClientBuilder;
    private final ConstructionProjectRepository constructionProjectRepository;
    private final LandsRepository landsRepository;

    public String analyzeProject(Integer projectId) {

        // التحقق من وجود المشروع
        ConstructionProject project = constructionProjectRepository.findConstructionProjectById(projectId);
        if (project == null) {
            return "Construction project not found";
        }


        // جلب الأرض المرتبطة بالمشروع
        Lands land = landsRepository.findLandsById(project.getLandId());
        if (land == null) {
            return "Land not found";
        }


        ChatClient chatClient = chatClientBuilder.build();

        String prompt = """
                You are an AI assistant for BAYTI, a construction management platform.

                Analyze this construction project and provide a short, practical recommendation
                for the project owner.

                Project type: %s
                Construction type: %s
                Land location: %s
                Land area: %s square meters
                Number of floors: %d
                Number of rooms: %d
                Budget: %s

                Provide:
                1. A short project assessment.
                2. Important things the owner should consider.
                3. A practical recommendation.

                Keep the response concise and easy to understand.
                """.formatted(
                project.getProjectType(),
                project.getConstructionType(),
                land.getLocation(),
                land.getArea(),
                project.getFloors(),
                project.getRoom(),
                project.getBudget()
        );

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}