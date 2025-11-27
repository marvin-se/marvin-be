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
CREATE TABLE USER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200),
    profile_pic_url VARCHAR(300),
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(300) NOT NULL,
    university_id BIGINT NOT NULL,
    phone_number VARCHAR(50),
    created_at TIMESTAMP,
    is_active BOOLEAN,
    FOREIGN KEY (university_id) REFERENCES university(id)
);
CREATE INDEX idx_user_university_id ON `USER` (`university_id`);
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
    FOREIGN KEY (user_id) REFERENCES user(id)
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
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (receiver_id) REFERENCES user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- =====================
-- FAVOURITES
-- =====================
CREATE TABLE FAVOURITE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);
