-- Create the schema dbo
CREATE SCHEMA IF NOT EXISTS dbo;

-- Create table for users in the dbo schema
CREATE TABLE dbo.users
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255)        NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    password_validation VARCHAR(255)        NOT NULL
);

-- Create table for user tokens in the dbo schema
CREATE TABLE dbo.Tokens
(
    token_validation VARCHAR(256) PRIMARY KEY,
    user_id          INT REFERENCES dbo.Users (id),
    created_at       BIGINT NOT NULL,
    last_used_at     BIGINT NOT NULL
);

-- Create table for products in the dbo schema
CREATE TABLE dbo.Products
(
    id          SERIAL PRIMARY KEY,
    user_id     INT REFERENCES dbo.Users (id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    region      VARCHAR(255) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL CHECK (price > 0)
);

-- Create table for images details in the dbo schema
CREATE TABLE dbo.Images
(
    id          VARCHAR(255) PRIMARY KEY,
    product_id  INT REFERENCES dbo.Products (id),
    url         TEXT NOT NULL
);