-- =====================
-- UNIVERSITIES
-- =====================
CREATE TABLE UNIVERSITY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    domain VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL
);

-- =====================
-- USERS
-- =====================
CREATE TABLE USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200),
    profile_pic_url VARCHAR(300),
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(300) NOT NULL,
    university_id BIGINT NOT NULL,
    phone_number VARCHAR(50),
    created_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (university_id) REFERENCES university(id)
);
CREATE INDEX idx_users_university_id ON `USERS` (`university_id`);
-- =====================
-- PRODUCTS
-- =====================
CREATE TABLE PRODUCT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2),
    category VARCHAR(100) NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_product_user_id ON `PRODUCT` (`user_id`);
CREATE INDEX idx_product_category ON `PRODUCT` (`category`);
CREATE INDEX idx_product_status ON `PRODUCT` (`status`);

-- =====================
-- IMAGES
-- =====================
CREATE TABLE IMAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
CREATE INDEX idx_image_product_id ON `IMAGE` (`product_id`);
-- =====================
-- MESSAGES
-- =====================
CREATE TABLE MESSAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    receiver_id BIGINT,
    conversation_id BIGINT,
    --product_id BIGINT,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE
    --FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
);
-- INDEX idx_message_product_id ON `MESSAGE` (`product_id`);
CREATE INDEX idx_message_conversation_id ON `MESSAGE` (`conversation_id`);
CREATE INDEX idx_message_sender_id ON `MESSAGE` (`sender_id`);
CREATE INDEX idx_message_receiver_id ON `MESSAGE` (`receiver_id`);
-- =====================
-- FAVOURITES
-- =====================
CREATE TABLE FAVOURITE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_favourite_product_id ON `FAVOURITE` (`product_id`);
CREATE INDEX idx_favourite_user_id ON `FAVOURITE` (`user_id`);

-- =====================
-- CONVERSATION
-- =====================
CREATE TABLE CONVERSATION (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_one_id BIGINT NOT NULL,
    user_two_id BIGINT NOT NULL,
    product_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_one_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (user_two_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
);

CREATE TABLE USERS_BLOCK(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (blocker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES users(id) ON DELETE CASCADE
);
