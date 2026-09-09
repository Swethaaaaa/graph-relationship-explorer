package com.swetha.graphexplorer;

import static org.assertj.core.api.Assertions.assertThat;

import com.swetha.graphexplorer.domain.enums.CompanySize;
import com.swetha.graphexplorer.domain.enums.ProficiencyLevel;
import com.swetha.graphexplorer.domain.node.Company;
import com.swetha.graphexplorer.domain.node.Skill;
import com.swetha.graphexplorer.domain.node.User;
import com.swetha.graphexplorer.domain.relationship.CollaboratedWith;
import com.swetha.graphexplorer.domain.relationship.HasSkill;
import com.swetha.graphexplorer.domain.relationship.WorksAt;
import com.swetha.graphexplorer.repository.CompanyRepository;
import com.swetha.graphexplorer.repository.SkillRepository;
import com.swetha.graphexplorer.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Verifies the Spring Data Neo4j domain model (Phase 2) actually persists
 * and reloads correctly against a real Neo4j instance, including the
 * self-referential USER_COLLABORATED_WITH_USER relationship, which is the
 * one place a naive equals()/toString() could recurse infinitely.
 * <p>
 * Requires a local Docker daemon (Testcontainers starts a real Neo4j
 * container). Skipped implicitly wherever Docker isn't available.
 */
@SpringBootTest
@Testcontainers
class GraphDomainModelIT {

    @Container
    static final Neo4jContainer<?> NEO4J = new Neo4jContainer<>("neo4j:5.23-community")
            .withAdminPassword("test12345");

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", NEO4J::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "test12345");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    void savesUserWithRelationshipsAndReloadsThem() {
        Company acme = companyRepository.save(Company.builder()
                .name("Acme Corp")
                .industry("Software")
                .size(CompanySize.MEDIUM)
                .foundedYear(2010)
                .build());

        Skill java = skillRepository.save(Skill.builder()
                .name("Java")
                .category("Programming Language")
                .build());

        User alice = User.builder()
                .fullName("Alice Smith")
                .email("alice@example.com")
                .title("Senior Engineer")
                .build();
        alice.getCompanies().add(WorksAt.builder()
                .company(acme)
                .jobTitle("Senior Engineer")
                .startDate(LocalDate.of(2020, 1, 1))
                .current(true)
                .build());
        alice.getSkills().add(HasSkill.builder()
                .skill(java)
                .proficiencyLevel(ProficiencyLevel.EXPERT)
                .yearsOfExperience(8)
                .build());

        User saved = userRepository.save(alice);
        User reloaded = userRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getCompanies()).hasSize(1);
        assertThat(reloaded.getCompanies().iterator().next().getCompany().getName()).isEqualTo("Acme Corp");
        assertThat(reloaded.getSkills()).hasSize(1);
        assertThat(reloaded.getSkills().iterator().next().getProficiencyLevel())
                .isEqualTo(ProficiencyLevel.EXPERT);
    }

    @Test
    void selfReferentialCollaborationRoundTripsWithoutStackOverflow() {
        User alice = userRepository.save(User.builder().fullName("Alice").email("alice2@example.com").build());
        User bob = userRepository.save(User.builder().fullName("Bob").email("bob2@example.com").build());

        alice.getCollaborations().add(CollaboratedWith.builder()
                .collaborator(bob)
                .context("Project X")
                .collaborationCount(3)
                .build());
        User saved = userRepository.save(alice);

        User reloaded = userRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getCollaborations()).hasSize(1);
        assertThat(reloaded.getCollaborations().iterator().next().getCollaborator().getFullName())
                .isEqualTo("Bob");
        // Exercises the Lombok-generated toString/equals on the User<->User cycle;
        // would StackOverflowError here if equals/toString weren't scoped to "id".
        assertThat(reloaded.toString()).contains("Alice");
        assertThat(reloaded).isEqualTo(reloaded);
    }
}
