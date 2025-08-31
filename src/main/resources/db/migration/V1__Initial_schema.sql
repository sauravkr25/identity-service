-- 1. Create the Users table
-- This table will store the primary information for every user.
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       full_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20) UNIQUE,
                       status VARCHAR(50) NOT NULL,
                       is_corporate_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. Create the Roles table
-- This table defines all possible roles in the system.
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Create the User-Roles join table
-- This table links users to their roles. A user can have multiple roles.
CREATE TABLE user_roles (
                            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- 4. Insert the initial roles into the Roles table
-- This pre-populates the system with the roles we need.
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_DRIVER'), ('ROLE_ADMIN');

-- Add a comment to confirm completion
-- COMMENT ON TABLE users IS 'ShareRide Identity Service tables created successfully.';