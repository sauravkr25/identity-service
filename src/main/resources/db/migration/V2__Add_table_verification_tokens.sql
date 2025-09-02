-- 5. Create the Verification Tokens table
-- This table stores tokens for verifying user email addresses.
CREATE TABLE verification_tokens (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     user_id UUID NOT NULL UNIQUE,
                                     token VARCHAR(255) NOT NULL UNIQUE,
                                     expiry_timestamp TIMESTAMPTZ NOT NULL,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                     CONSTRAINT fk_verification_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


-- Optional indexes for performance
CREATE INDEX idx_verification_token ON verification_tokens (token);
CREATE INDEX idx_verification_user_id ON verification_tokens (user_id);
