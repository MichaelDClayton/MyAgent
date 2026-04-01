-- Create failed_messages table
CREATE TABLE IF NOT EXISTS failed_messages (
    id BIGSERIAL PRIMARY KEY,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_RETRY',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexing the status for faster lookups by the Scheduler
CREATE INDEX idx_failed_messages_status ON failed_messages(status);

-- Create Tenants table
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Common multi-tenant tables
CREATE TABLE IF NOT EXISTS item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES item(id) ON DELETE CASCADE,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    remaining_capacity INT NOT NULL DEFAULT 1,
    CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES item(id) ON DELETE CASCADE,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS knowledge_base (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    answer TEXT NOT NULL
);

-- Enable RLS on all multi-tenant tables
ALTER TABLE item ENABLE ROW LEVEL SECURITY;
ALTER TABLE availability ENABLE ROW LEVEL SECURITY;
ALTER TABLE bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE knowledge_base ENABLE ROW LEVEL SECURITY;


-- Create Policies for RLS
-- We use a session variable 'app.current_tenant_id' to differentiate tenants.
-- Ensure the variable is set with `SET app.current_tenant_id = '...'` before queries.

CREATE POLICY tenant_item_isolation ON item
    FOR ALL USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

CREATE POLICY tenant_availability_isolation ON availability
    FOR ALL USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

CREATE POLICY tenant_bookings_isolation ON bookings
    FOR ALL USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

CREATE POLICY tenant_knowledge_base_isolation ON knowledge_base
    FOR ALL USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Seed initial tenant/data for testing
-- (In a real app, this would be done through a system API)
INSERT INTO tenants (id, name, api_key) VALUES 
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Acme Tours', 'acme-api-key-123'),
('550e8400-e29b-41d4-a716-446655440000', 'Globex Tours', 'globex-secret');

-- Acme Sample Data
-- Since current_setting('app.current_tenant_id') won't be set during init, 
-- we temporary disable RLS for seeding IF we were using the same connection.
-- During init script, the superuser bypasses RLS anyway.

INSERT INTO item (id, tenant_id, name, description, price) 
VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Grand Canyon Tour', 'A beautiful tour of the Grand Canyon.', 99.99);

INSERT INTO availability (tenant_id, item_id, start_time, end_time, remaining_capacity)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2026-04-01 10:00:00+00', '2026-04-01 12:00:00+00', 10);

INSERT INTO knowledge_base (tenant_id, question, answer)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'How long is the tour?', 'The Grand Canyon tour lasts approximately 2 hours.');
