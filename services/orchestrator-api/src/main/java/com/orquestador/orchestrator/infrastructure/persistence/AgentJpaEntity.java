package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Agent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentJpaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    @Column(nullable = false)
    private double temperature;

    public static AgentJpaEntity fromDomain(Agent domain) {
        if (domain == null) return null;
        return new AgentJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getRole(),
                domain.getSystemPrompt(),
                domain.getModelName(),
                domain.getTemperature()
        );
    }

    public Agent toDomain() {
        return new Agent(
                this.id,
                this.name,
                this.role,
                this.systemPrompt,
                this.modelName,
                this.temperature
        );
    }
}
