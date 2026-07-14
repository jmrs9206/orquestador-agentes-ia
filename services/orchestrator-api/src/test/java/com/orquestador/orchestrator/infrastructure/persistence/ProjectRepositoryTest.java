package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectRepository;
import com.orquestador.orchestrator.domain.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class ProjectRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldSaveAndRetrieveProject() {
        Project project = new Project(
                "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "test-project",
                "Test Project",
                "A project for testing",
                ProjectStatus.DRAFT,
                "/tmp/test-project",
                "main",
                ".ai",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        Project saved = projectRepository.save(project);
        assertNotNull(saved);
        assertEquals("test-project", saved.getKey());

        Optional<Project> retrieved = projectRepository.findById("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        assertTrue(retrieved.isPresent());
        assertEquals("Test Project", retrieved.get().getName());
        assertEquals(ProjectStatus.DRAFT, retrieved.get().getStatus());
    }

    @Test
    void shouldFilterArchivedProjects() {
        Project activeProject = new Project(
                "active-id-1",
                "active-project",
                "Active Project",
                "Active",
                ProjectStatus.ACTIVE,
                "/tmp/active",
                "main",
                ".ai",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        Project archivedProject = new Project(
                "archived-id-1",
                "archived-project",
                "Archived Project",
                "Archived",
                ProjectStatus.ARCHIVED,
                "/tmp/archived",
                "main",
                ".ai",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectRepository.save(activeProject);
        projectRepository.save(archivedProject);

        List<Project> allProjects = projectRepository.findAll(true);
        assertTrue(allProjects.size() >= 2);

        List<Project> activeOnly = projectRepository.findAll(false);
        boolean containsArchived = activeOnly.stream()
                .anyMatch(p -> p.getStatus() == ProjectStatus.ARCHIVED);
        assertFalse(containsArchived);
    }
}
