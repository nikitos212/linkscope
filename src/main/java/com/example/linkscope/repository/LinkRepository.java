package com.example.linkscope.repository;

import com.example.linkscope.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {

    Optional<LinkEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
