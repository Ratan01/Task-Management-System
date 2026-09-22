package com.taskapp.audit.repository;

import com.taskapp.audit.entity.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByActorIdOrderByOccurredAtDesc(Long actorId, Pageable page);

    List<AuditLog> findByActionOrderByOccurredAtDesc(String action, Pageable page);

    List<AuditLog> findByOccurredAtAfterOrderByOccurredAtDesc(Instant after, Pageable page);

    @Query("select count(a) from AuditLog a where a.action = :action and a.occurredAt >= :since")
    long countByActionSince(@Param("action") String action, @Param("since") Instant since);

    @Query("select a.action, count(a) from AuditLog a where a.occurredAt >= :since group by a.action")
    List<Object[]> countByActionSinceGrouped(@Param("since") Instant since);
}