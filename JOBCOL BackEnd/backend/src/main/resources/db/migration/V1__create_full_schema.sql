-- =========================================
-- USERS
-- =========================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    keycloak_user_id VARCHAR(100),
    email VARCHAR(320) NOT NULL UNIQUE,
    username VARCHAR(120) NOT NULL,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60) NOT NULL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    img_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL
);

-- =========================================
-- PROFILES (1-1 con users)
-- =========================================
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    skills VARCHAR(500),
    experience VARCHAR(1000),
    location VARCHAR(255),
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    average_rating DOUBLE PRECISION,
    total_reviews INTEGER,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- EMPLOYER PROFILES (1-1 con users)
-- =========================================
CREATE TABLE employer_profiles (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255),
    description VARCHAR(1000),
    location VARCHAR(255),
    average_rating DOUBLE PRECISION,
    total_jobs_posted INTEGER,
    total_reviews INTEGER,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_employer_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
-- =========================================
-- OFFERS (Many-to-One con users como employer)
-- =========================================
CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    category VARCHAR(255),
    location VARCHAR(255),
    salary_range INTEGER,
    status VARCHAR(50) NOT NULL,
    publication_date TIMESTAMP,
    employer_id BIGINT NOT NULL,
    CONSTRAINT fk_offer_employer
        FOREIGN KEY (employer_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- POSTULATIONS (Many-to-One con users como worker y offers)
-- =========================================
CREATE TABLE postulations (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    application_date TIMESTAMP NOT NULL,
    calification INTEGER NOT NULL DEFAULT 0,
    worker_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    CONSTRAINT fk_postulation_worker
        FOREIGN KEY (worker_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_postulation_offer
        FOREIGN KEY (offer_id)
        REFERENCES offers(id)
        ON DELETE CASCADE
);

-- =========================================
-- CONTRACTS (1-1 con postulation)
-- =========================================
CREATE TABLE contracts (
    id BIGSERIAL PRIMARY KEY,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    agreed_amount INTEGER,
    worker_finished BOOLEAN NOT NULL DEFAULT FALSE,
    employer_finished BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL,
    postulation_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_contract_postulation
        FOREIGN KEY (postulation_id)
        REFERENCES postulations(id)
        ON DELETE CASCADE
);

-- =========================================
-- REVIEWS (Many-to-One con users)
-- =========================================
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    rating INTEGER,
    comment VARCHAR(1000),
    author_type VARCHAR(50) NOT NULL,
    image_url VARCHAR(500),
    review_date TIMESTAMP,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    reviewed_user_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    CONSTRAINT fk_review_reviewed_user
        FOREIGN KEY (reviewed_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_review_reviewer
        FOREIGN KEY (reviewer_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- NOTIFICATIONS
-- =========================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- MESSAGES
-- =========================================
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(2000) NOT NULL,
    sent_date TIMESTAMP NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_message_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
-- =========================================
-- VERIFICATION CODES (Many-to-One con users)
-- =========================================
CREATE TABLE verification_code (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    expiration TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_verification_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- POSTS (Many-to-One con users)
-- =========================================
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_post_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- MEDIA (Many-to-One con posts)
-- =========================================
CREATE TABLE media (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(20) NOT NULL, -- IMAGE / VIDEO
    post_id BIGINT NOT NULL,

    CONSTRAINT fk_media_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE
);

-- =========================================
-- LIKES (Many-to-One con users y posts)
-- =========================================
CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT fk_like_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_like_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_like UNIQUE (user_id, post_id)
);

-- =========================================
-- COMMENTS (Many-to-One con users y posts)
-- =========================================
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE
);