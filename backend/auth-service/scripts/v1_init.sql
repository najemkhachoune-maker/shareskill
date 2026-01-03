CREATE TABLE users (
    id UUID PRIMARY KEY,
    keycloak_user_id VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    bio TEXT,
    profile_picture_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    has_learner_profile BOOLEAN DEFAULT FALSE,
    has_teacher_profile BOOLEAN DEFAULT FALSE,
    learner_level INTEGER DEFAULT 1,
    teacher_level INTEGER DEFAULT 1,
    available_tokens INTEGER DEFAULT 0,
    total_tokens_earned INTEGER DEFAULT 0,
    quests_completed INTEGER DEFAULT 0,
    students_taught INTEGER DEFAULT 0,
    average_rating_as_learner DECIMAL(3,2) DEFAULT 0.0,
    average_rating_as_teacher DECIMAL(3,2) DEFAULT 0.0,
    gdpr_consent_date TIMESTAMP,
    data_anonymized BOOLEAN DEFAULT FALSE,
    encrypted_personal_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

CREATE INDEX idx_users_keycloak_id ON users(keycloak_user_id);
CREATE INDEX idx_users_email ON users(email);
