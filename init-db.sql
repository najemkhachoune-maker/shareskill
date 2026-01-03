-- SkillVerse Production Database Initialization
-- This script creates the necessary schemas and tables

-- Create schemas for each microservice
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS profile;
CREATE SCHEMA IF NOT EXISTS chat;
CREATE SCHEMA IF NOT EXISTS booking;
CREATE SCHEMA IF NOT EXISTS reputation;
CREATE SCHEMA IF NOT EXISTS notification;
CREATE SCHEMA IF NOT EXISTS matching;

-- Grant permissions to postgres user (default superuser)
GRANT ALL PRIVILEGES ON SCHEMA auth TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA profile TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA chat TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA booking TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA reputation TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA notification TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA matching TO postgres;

-- Grant all on all tables in schemas
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA profile TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA chat TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA booking TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA reputation TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA notification TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA matching TO postgres;

-- Grant all on all sequences in schemas
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA profile TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA chat TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA booking TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA reputation TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA notification TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA matching TO postgres;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create indexes for performance
-- (Tables will be created by JPA/Hibernate on first run)

COMMENT ON SCHEMA auth IS 'Authentication and user management';
COMMENT ON SCHEMA profile IS 'User profiles and skills';
COMMENT ON SCHEMA chat IS 'Real-time messaging';
COMMENT ON SCHEMA booking IS 'Session bookings';
COMMENT ON SCHEMA reputation IS 'User reputation and ratings';
COMMENT ON SCHEMA notification IS 'User notifications';
COMMENT ON SCHEMA matching IS 'User matching logic';
