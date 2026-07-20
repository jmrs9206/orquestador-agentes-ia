package com.orquestador.orchestrator.domain;

public class Agent {
    private final String id;
    private final String name;
    private final String role;
    private final String systemPrompt;
    private final String modelName;
    private final double temperature;

    public Agent(String id, String name, String role, String systemPrompt, String modelName, double temperature) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.systemPrompt = systemPrompt;
        this.modelName = modelName;
        this.temperature = temperature;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getSystemPrompt() { return systemPrompt; }
    public String getModelName() { return modelName; }
    public double getTemperature() { return temperature; }
}
