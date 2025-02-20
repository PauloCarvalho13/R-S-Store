BEGIN;

-- Insert user
INSERT INTO dbo.users (name, email, password_validation)
VALUES ('admin', 'admin', 'admin');

COMMIT;