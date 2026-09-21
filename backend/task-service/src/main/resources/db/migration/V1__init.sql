CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(2000),
                       owner_id BIGINT NOT NULL,
                       due_date DATE,
                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX idx_tasks_owner ON tasks(owner_id);
CREATE INDEX idx_tasks_due_status ON tasks(due_date, status);