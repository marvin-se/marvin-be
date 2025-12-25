-- =====================
-- UNIVERSITIES
-- =====================
CREATE TABLE university (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    uni_domain VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL
);

-- =====================
-- USERS
-- =====================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200),
    profile_pic_url VARCHAR(300),
    description VARCHAR(1000),
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(300) NOT NULL,
    university_id BIGINT NOT NULL,
    phone_number VARCHAR(50),
    created_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (university_id) REFERENCES university(id)
);
CREATE INDEX idx_users_university_id ON `users` (`university_id`);
-- =====================
-- PRODUCTS
-- =====================
CREATE TABLE product (
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
CREATE INDEX idx_product_user_id ON `product` (`user_id`);
CREATE INDEX idx_product_category ON `product` (`category`);
CREATE INDEX idx_product_status ON `product` (`status`);

-- =====================
-- IMAGES
-- =====================
CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
CREATE INDEX idx_image_product_id ON `image` (`product_id`);

-- =====================
-- FAVOURITES
-- =====================
CREATE TABLE favourite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_favourite_product_id ON `favourite` (`product_id`);
CREATE INDEX idx_favourite_user_id ON `favourite` (`user_id`);

-- =====================
-- CONVERSATION
-- =====================
CREATE TABLE conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_one_id BIGINT,
    user_two_id BIGINT,
    product_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_one_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (user_two_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
);
CREATE INDEX idx_conversation_user_one_id ON `conversation` (`user_one_id`);
CREATE INDEX idx_conversation_user_two_id ON `conversation` (`user_two_id`);
CREATE INDEX idx_conversation_product_id ON `conversation` (`product_id`);
-- =====================
-- MESSAGES
-- =====================
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    receiver_id BIGINT,
    conversation_id BIGINT,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE
);
CREATE INDEX idx_message_conversation_id ON `message` (`conversation_id`);
CREATE INDEX idx_message_sender_id ON `message` (`sender_id`);
CREATE INDEX idx_message_receiver_id ON `message` (`receiver_id`);
-- =====================
-- USERS_BLOCK
-- =====================
CREATE TABLE users_block(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (blocker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_users_block_blocker_id ON `users_block` (`blocker_id`);
CREATE INDEX idx_users_block_blocked_id ON `users_block` (`blocked_id`);
-- =====================
-- TRANSACTIONS
-- =====================
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT,
    seller_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE (product_id)
);
CREATE UNIQUE INDEX idx_transactions_product_id ON `transactions` (`product_id`);
CREATE INDEX idx_transactions_buyer_id ON `transactions` (`buyer_id`);
CREATE INDEX idx_transactions_seller_id ON `transactions` (`seller_id`);

-- =====================
-- TOKENS
-- =====================
CREATE TABLE token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_token_user_id ON `token` (`user_id`);