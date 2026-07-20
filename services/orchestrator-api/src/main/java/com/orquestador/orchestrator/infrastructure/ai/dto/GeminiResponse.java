package com.orquestador.orchestrator.infrastructure.ai.dto;

import java.util.List;

public class GeminiResponse {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;

    public GeminiResponse() {}

    public GeminiResponse(List<Candidate> candidates, UsageMetadata usageMetadata) {
        this.candidates = candidates;
        this.usageMetadata = usageMetadata;
    }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    public UsageMetadata getUsageMetadata() { return usageMetadata; }
    public void setUsageMetadata(UsageMetadata usageMetadata) { this.usageMetadata = usageMetadata; }

    public String extractResponseText() {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }
        Candidate firstCandidate = candidates.get(0);
        if (firstCandidate.getContent() == null || firstCandidate.getContent().getParts() == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Part part : firstCandidate.getContent().getParts()) {
            if (part.getText() != null) {
                sb.append(part.getText());
            }
        }
        return sb.toString();
    }

    public static class Candidate {
        private Content content;
        private String finishReason;

        public Candidate() {}

        public Candidate(Content content, String finishReason) {
            this.content = content;
            this.finishReason = finishReason;
        }

        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }

        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    public static class Content {
        private String role;
        private List<Part> parts;

        public Content() {}

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    public static class Part {
        private String text;

        public Part() {}

        public Part(String text) {
            this.text = text;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class UsageMetadata {
        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;

        public UsageMetadata() {}

        public UsageMetadata(Integer promptTokenCount, Integer candidatesTokenCount, Integer totalTokenCount) {
            this.promptTokenCount = promptTokenCount;
            this.candidatesTokenCount = candidatesTokenCount;
            this.totalTokenCount = totalTokenCount;
        }

        public Integer getPromptTokenCount() { return promptTokenCount; }
        public void setPromptTokenCount(Integer promptTokenCount) { this.promptTokenCount = promptTokenCount; }

        public Integer getCandidatesTokenCount() { return candidatesTokenCount; }
        public void setCandidatesTokenCount(Integer candidatesTokenCount) { this.candidatesTokenCount = candidatesTokenCount; }

        public Integer getTotalTokenCount() { return totalTokenCount; }
        public void setTotalTokenCount(Integer totalTokenCount) { this.totalTokenCount = totalTokenCount; }
    }
}
