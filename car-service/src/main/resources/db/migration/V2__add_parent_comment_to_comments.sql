ALTER TABLE comments
    ADD COLUMN parent_comment_id BIGINT,
    ADD CONSTRAINT fk_comments_parent_comment FOREIGN KEY (parent_comment_id) REFERENCES comments(id);

CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);