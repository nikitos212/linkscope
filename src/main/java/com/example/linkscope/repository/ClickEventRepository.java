package com.example.linkscope.repository;

import com.example.linkscope.entity.ClickEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClickEventRepository extends JpaRepository<ClickEventEntity, UUID> {

    List<ClickEventEntity> findTop10ByLink_IdOrderByClickedAtDesc(UUID linkId);

    @Query("select count(distinct c.ipAddress) from ClickEventEntity c where c.link.id = :linkId and c.ipAddress is not null")
    long countDistinctIpByLinkId(@Param("linkId") UUID linkId);

    @Query("select max(c.clickedAt) from ClickEventEntity c where c.link.id = :linkId")
    LocalDateTime findLastClickedAtByLinkId(@Param("linkId") UUID linkId);
}
