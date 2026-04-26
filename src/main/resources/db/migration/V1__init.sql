CREATE TABLE links (
    id UUID PRIMARY KEY,
    original_url TEXT NOT NULL,
    short_code VARCHAR(30) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    click_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE INDEX idx_links_created_at ON links (created_at);

CREATE TABLE click_events (
    id UUID PRIMARY KEY,
    link_id UUID NOT NULL REFERENCES links(id) ON DELETE CASCADE,
    clicked_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referer TEXT
);

CREATE INDEX idx_click_events_link_id ON click_events (link_id);
CREATE INDEX idx_click_events_clicked_at ON click_events (clicked_at);
