package com.orquestador.orchestrator.infrastructure.ai.dto;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    private SystemInstruction systemInstruction;
    private List<Content> contents;
    private GenerationConfig generationConfig;

    public GeminiRequest() {}

    public GeminiRequest(SystemInstruction systemInstruction, List<Content> contents, GenerationConfig generationConfig) {
        this.systemInstruction = systemInstruction;
        this.contents = contents;
        this.generationConfig = generationConfig;
    }

    public SystemInstruction getSystemInstruction() { return systemInstruction; }
    public void setSystemInstruction(SystemInstruction systemInstruction) { this.systemInstruction = systemInstruction; }

    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }

    public GenerationConfig getGenerationConfig() { return generationConfig; }
    public void setGenerationConfig(GenerationConfig generationConfig) { this.generationConfig = generationConfig; }

    public static class SystemInstruction {
        private List<Part> parts;

        public SystemInstruction() {}

        public SystemInstruction(String text) {
            this.parts = Collections.singletonList(new Part(text));
        }

        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    public static class Content {
        private String role;
        private List<Part> parts;

        public Content() {}

        public Content(String role, String text) {
            this.role = role;
            this.parts = Collections.singletonList(new Part(text));
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

    public static class GenerationConfig {
        private Double temperature;

        public GenerationConfig() {}

        public GenerationConfig(Double temperature) {
            this.temperature = temperature;
        }

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
    }
}
