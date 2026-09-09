package com.swetha.graphexplorer.graph;

import static org.assertj.core.api.Assertions.assertThat;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.enums.ProjectStatus;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.dto.request.CreateCompanyRequest;
import com.swetha.graphexplorer.dto.request.CreateProjectRequest;
import com.swetha.graphexplorer.dto.request.CreateRelationshipRequest;
import com.swetha.graphexplorer.dto.request.CreateSkillRequest;
import com.swetha.graphexplorer.dto.request.CreateTeamRequest;
import com.swetha.graphexplorer.dto.request.CreateTechnologyRequest;
import com.swetha.graphexplorer.dto.request.CreateUserRequest;
import com.swetha.graphexplorer.dto.response.CompanyResponse;
import com.swetha.graphexplorer.dto.response.GraphNodeDto;
import com.swetha.graphexplorer.dto.response.GraphResponse;
import com.swetha.graphexplorer.dto.response.ProjectResponse;
import com.swetha.graphexplorer.dto.response.ShortestPathResponse;
import com.swetha.graphexplorer.dto.response.SkillResponse;
import com.swetha.graphexplorer.dto.response.TeamResponse;
import com.swetha.graphexplorer.dto.response.TechnologyResponse;
import com.swetha.graphexplorer.dto.response.UserResponse;
import com.swetha.graphexplorer.service.CompanyService;
import com.swetha.graphexplorer.service.ProjectService;
import com.swetha.graphexplorer.service.RelationshipService;
import com.swetha.graphexplorer.service.SkillService;
import com.swetha.graphexplorer.service.TeamService;
import com.swetha.graphexplorer.service.TechnologyService;
import com.swetha.graphexplorer.service.UserService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end test of Phase 4 (graph traversal) built on top of Phase 3
 * (entity + relationship creation): builds a small realistic graph through
 * the real service layer, then verifies expand/commonConnections/
 * shortestPath read it back correctly from Neo4j. Requires a local Docker
 * daemon (Testcontainers).
 */
@SpringBootTest
@Testcontainers
class GraphTraversalIT {

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
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TechnologyService technologyService;
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private GraphTraversalService graphTraversalService;
    @Autowired
    private UserDiscoveryService userDiscoveryService;

    private UserResponse alice;
    private UserResponse bob;
    private CompanyResponse acme;
    private TeamResponse backendTeam;
    private SkillResponse javaSkill;
    private ProjectResponse websiteProject;
    private TechnologyResponse reactTech;

    @BeforeEach
    void seedGraph() {
        alice = userService.createUser(new CreateUserRequest("Alice Smith", "alice@example.com", "Engineer", null, null));
        bob = userService.createUser(new CreateUserRequest("Bob Jones", "bob@example.com", "Engineer", null, null));
        acme = companyService.createCompany(new CreateCompanyRequest("Acme Corp", "Software", null, null, 2010));
        backendTeam = teamService.createTeam(new CreateTeamRequest("Backend", "Backend team", "Engineering"));
        javaSkill = skillService.createSkill(new CreateSkillRequest("Java", "Programming Language"));
        websiteProject = projectService.createProject(
                new CreateProjectRequest("Website Redesign", null, ProjectStatus.ACTIVE, null, null));
        reactTech = technologyService.createTechnology(new CreateTechnologyRequest("React", "Framework"));

        createRelationship(RelationshipType.USER_WORKS_AT_COMPANY, alice.id(), acme.id(),
                Map.of("jobTitle", "Engineer", "startDate", "2020-01-01", "current", true));
        createRelationship(RelationshipType.USER_WORKS_AT_COMPANY, bob.id(), acme.id(),
                Map.of("jobTitle", "Engineer", "startDate", "2021-01-01", "current", true));
        createRelationship(RelationshipType.USER_MEMBER_OF_TEAM, alice.id(), backendTeam.id(),
                Map.of("role", "Member", "joinedDate", "2020-01-01"));
        createRelationship(RelationshipType.USER_HAS_SKILL, alice.id(), javaSkill.id(),
                Map.of("proficiencyLevel", "EXPERT", "yearsOfExperience", 8));
        createRelationship(RelationshipType.USER_HAS_SKILL, bob.id(), javaSkill.id(),
                Map.of("proficiencyLevel", "INTERMEDIATE", "yearsOfExperience", 3));
        createRelationship(RelationshipType.USER_WORKED_ON_PROJECT, alice.id(), websiteProject.id(),
                Map.of("role", "Lead", "startDate", "2022-01-01"));
        createRelationship(RelationshipType.PROJECT_USES_TECHNOLOGY, websiteProject.id(), reactTech.id(),
                Map.of("usageContext", "frontend framework"));
    }

    private void createRelationship(RelationshipType type, String sourceId, String targetId, Map<String, Object> props) {
        relationshipService.createRelationship(new CreateRelationshipRequest(type, sourceId, targetId, props));
    }

    @Test
    void expandDepth1FromAlice_returnsHerDirectNeighborsAndEdges() {
        GraphResponse graph = graphTraversalService.expand(EntityType.USER, alice.id(), 1);

        List<String> nodeNames = graph.nodes().stream().map(GraphNodeDto::name).toList();
        assertThat(nodeNames).containsExactlyInAnyOrder(
                "Alice Smith", "Acme Corp", "Backend", "Java", "Website Redesign");
        assertThat(graph.edges()).hasSize(4);
    }

    @Test
    void expandDepth2FromAlice_reachesTechnologyThroughProjectAndBobThroughCompany() {
        GraphResponse graph = graphTraversalService.expand(EntityType.USER, alice.id(), 2);

        List<String> nodeNames = graph.nodes().stream().map(GraphNodeDto::name).toList();
        assertThat(nodeNames).contains("React", "Bob Jones");
    }

    @Test
    void commonConnections_betweenAliceAndBob_isAcmeAndJava() {
        List<GraphNodeDto> common = userDiscoveryService.commonConnections(alice.id(), bob.id());

        assertThat(common.stream().map(GraphNodeDto::name)).containsExactlyInAnyOrder("Acme Corp", "Java");
    }

    @Test
    void shortestPath_betweenAliceAndBob_hasLengthTwoThroughACommonNode() {
        ShortestPathResponse path = userDiscoveryService.shortestPath(alice.id(), bob.id());

        assertThat(path.length()).isEqualTo(2);
        assertThat(path.nodes().get(0).name()).isEqualTo("Alice Smith");
        assertThat(path.nodes().get(path.nodes().size() - 1).name()).isEqualTo("Bob Jones");
    }
}
