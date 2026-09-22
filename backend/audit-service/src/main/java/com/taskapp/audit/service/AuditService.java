package com.taskapp.audit.service;

import com.taskapp.audit.dto.AuditEventDto;
import com.taskapp.audit.entity.AuditLog;
import com.taskapp.audit.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog record(AuditEventDto dto) {
        AuditLog log = new AuditLog();
        log.setActorId(dto.actorId());
        log.setActorUsername(dto.actorUsername());
        log.setAction(dto.action());
        log.setResourceType(dto.resourceType());
        log.setResourceId(dto.resourceId());
        log.setDetails(dto.details());
        log.setOccurredAt(dto.occurredAt() != null ? dto.occurredAt() : Instant.now());
        return repository.save(log);
    }

    public List<AuditLog> findByActor(Long actorId, int limit) {
        return repository.findByActorIdOrderByOccurredAtDesc(actorId, PageRequest.of(0, limit));
    }

    public List<AuditLog> findByAction(String action, int limit) {
        return repository.findByActionOrderByOccurredAtDesc(action, PageRequest.of(0, limit));
    }

    public List<AuditLog> recent(Instant since, int limit) {
        return repository.findByOccurredAtAfterOrderByOccurredAtDesc(since, PageRequest.of(0, limit));
    }

    public Map<String, Long> countsSince(Instant since) {
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : repository.countByActionSinceGrouped(since)) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }
}