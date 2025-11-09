-- V4: Create tables for machine-to-machine (M2M) service client authentication
-- using the standard 'oauth_clients' table name.

-- Step 1: Add the new, specific role for the ride-booking service.
INSERT INTO roles (name) VALUES ('ROLE_SERVICE');


-- Step 2: Create the oauth_clients table.
CREATE TABLE oauth_clients (
                               client_id VARCHAR(255) PRIMARY KEY,
                               client_secret_hash VARCHAR(255) NOT NULL,
                               description TEXT,
                               status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE oauth_clients IS 'Stores credentials for M2M OAuth 2.0 clients (services).';


-- Step 3: Create the 'oauth_client_roles' join table.
-- This table links clients from the oauth_clients table to their assigned roles.
CREATE TABLE oauth_client_roles (
                                    client_id VARCHAR(255) NOT NULL REFERENCES oauth_clients(client_id) ON DELETE CASCADE,
                                    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,

    -- The primary key is the combination of the two, preventing duplicate role assignments.
                                    PRIMARY KEY (client_id, role_id)
);

COMMENT ON TABLE oauth_client_roles IS 'Join table linking OAuth clients to their assigned roles.';