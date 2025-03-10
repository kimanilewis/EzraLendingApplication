#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create database if it doesn't exist
    -- This is redundant since POSTGRES_DB env already creates it, but just to be safe:
    SELECT 'CREATE DATABASE lending' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'lending');

    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE lending TO postgres;
EOSQL

echo "Database initialization completed"