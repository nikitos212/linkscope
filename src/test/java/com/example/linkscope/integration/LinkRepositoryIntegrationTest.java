package com.example.linkscope.integration;

import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LinkRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("linkscope_db")
            .withUsername("linkscope")
            .withPassword("linkscope");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private LinkRepository linkRepository;

    @Test
    void shouldPersistAndReadLinkByShortCode() {
        LinkEntity entity = LinkEntity.builder()
                .id(UUID.randomUUID())
                .originalUrl("https://example.com/integration")
                .shortCode("itest01")
                .active(true)
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        linkRepository.saveAndFlush(entity);

        Optional<LinkEntity> loaded = linkRepository.findByShortCode("itest01");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getOriginalUrl()).isEqualTo("https://example.com/integration");
    }
}
