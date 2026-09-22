CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    actor_id BIGINT,
    actor_username VARCHAR(100),
    action VARCHAR(64) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    resource_id VARCHAR(64),
    details VARCHAR(4000),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_occurred ON audit_logs(occurred_at);