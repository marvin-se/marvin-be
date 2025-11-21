-- =====================
-- UNIVERSITIES
-- =====================
CREATE TABLE universities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    domain VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL
);

-- =====================
-- USERS
-- =====================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200),
    profile_pic_url VARCHAR(300),
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(300) NOT NULL,
    university BIGINT NOT NULL,
    phone_number VARCHAR(50),
    created_at TIMESTAMP,
    is_active BOOLEAN,
    FOREIGN KEY (university) REFERENCES universities(id)
);

-- =====================
-- PRODUCTS
-- =====================
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    price INT,
    category VARCHAR(100) NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- =====================
-- IMAGES
-- =====================
CREATE TABLE images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =====================
-- MESSAGES
-- =====================
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    content TEXT,
    is_read BOOLEAN,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =====================
-- FAVOURITES
-- =====================
CREATE TABLE favourites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
