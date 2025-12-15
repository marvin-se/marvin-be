INSERT INTO UNIVERSITY (name, domain, city) VALUES
('Istanbul Technical University (ITU)', 'itu.edu.tr', 'Istanbul'),
('Middle East Technical University (METU)', 'metu.edu.tr', 'Ankara'),
('Yildiz Technical University (YTU)', 'ytu.edu.tr', 'Istanbul'),
('Bogazici University', 'boun.edu.tr', 'Istanbul'),
('Hacettepe University', 'hacettepe.edu.tr', 'Ankara');

INSERT INTO USERS (full_name, profile_pic_url, email, password_hash, university_id, phone_number, created_at, is_active , is_verified , description)
VALUES
('Ayşe Yılmaz', 'https://pics.com/p1.jpg', 'ayse@itu.edu.tr', '$2a$10$KCujF5.G02ciqrK44bP4v.5BZcuEV/4qQnkHyGQ8lBOkqmg6nAj62', 1, '5551112233', NOW(), TRUE, TRUE, 'Computer Engineering student at ITU. Interested in electronics and gadgets.'),
('Mehmet Demir', 'https://pics.com/p2.jpg', 'mehmet@metu.edu.tr', 'hash456', 2, '5554445566', NOW(), TRUE, TRUE, 'Mechanical Engineering student at METU. Loves furniture and home decor.'),
('Elif Acar', NULL, 'elif@ytu.edu.tr', 'hash789', 3, '5558889977', NOW(), TRUE, TRUE, 'Industrial Design student at YTU. Passionate about stationery and art supplies.'),
('Can Koç', 'https://pics.com/p4.jpg', 'can@boun.edu.tr', 'hash101', 4, '5550909090', NOW(), TRUE, TRUE, 'Economics student at Bogazici University. Enjoys reading and writing.'),
('Zeynep Er', NULL, 'zeynep@hacettepe.edu.tr', 'hash202', 5, '5553030303', NOW(), FALSE, TRUE, 'Medicine student at Hacettepe University. Interested in health and wellness products.'),
('Hilal Kartal', NULL, 'kartalh21@itu.edu.tr', '$2a$10$KCujF5.G02ciqrK44bP4v.5BZcuEV/4qQnkHyGQ8lBOkqmg6nAj62', 1, '5383740242', '2025-12-10T16:43:36.8779941', TRUE, TRUE, 'Architecture student at ITU. Loves design and creativity.'),
('Selin Yılmaz', NULL, 'yilmazsel21@itu.edu.tr', '$2a$10$KCujF5.G02ciqrK44bP4v.5BZcuEV/4qQnkHyGQ8lBOkqmg6nAj62', 1, '5383740242', '2025-12-10T16:43:36.8779941', TRUE, TRUE, NULL),
('Zeliha Melek Bekdemir', NULL, 'bekdemir22@itu.edu.tr', '$2a$10$KCujF5.G02ciqrK44bP4v.5BZcuEV/4qQnkHyGQ8lBOkqmg6nAj62', 1, '5383740242', '2025-12-10T16:43:36.8779941', TRUE, TRUE, NULL),
('Alper Daşgın', NULL, 'dasgin21@itu.edu.tr', '$2a$10$KCujF5.G02ciqrK44bP4v.5BZcuEV/4qQnkHyGQ8lBOkqmg6nAj62', 1, '5383740242', '2025-12-10T16:43:36.8779941', TRUE, TRUE, NULL);

INSERT INTO PRODUCT (title, description, price, category, status, created_at, user_id, updated_at) VALUES
('Casio Scientific Calculator', 'Used but works perfectly.', 200, 'ELECTRONICS', 'AVAILABLE', NOW(), 1, NOW()),
('Desk Lamp', 'LED desk lamp with adjustable arm.', 150, 'HOME', 'AVAILABLE', NOW(), 2, NOW()),
('Computer Chair', 'Ergonomic chair (blue color)', 700, 'FURNITURE', 'SOLD', NOW(), 1, NOW()),
('Mechanical Keyboard', 'Red switches, RGB lights.', 850, 'ELECTRONICS', 'AVAILABLE', NOW(), 3, NOW()),
('Graphing Notebook Set', '5 notebooks, A4 size.', 60, 'STATIONERY', 'AVAILABLE', NOW(), 4, NOW());

INSERT INTO IMAGE (product_id, image_url) VALUES
(1, 'https://pics.com/calculator1.jpg'),
(1, 'https://pics.com/calculator2.jpg'),
(2, 'https://pics.com/lamp1.jpg'),
(3, 'https://pics.com/chair1.jpg'),
(4, 'https://pics.com/keyboard1.jpg');


INSERT INTO CONVERSATION (id, user_one_id, user_two_id, product_id, created_at) VALUES
(1, 2, 1, 1, '2025-11-01 09:50:00'),
(2, 3, 1, 3, '2025-11-02 12:25:00'),
(3, 4, 3, 4, '2025-11-03 09:10:00'),
(4, 5, 1, 5, '2025-11-04 14:00:00');

-- MESSAGE örnek verileri (artık product_id değil conversation_id kullanılıyor)
INSERT INTO MESSAGE (sender_id, receiver_id, conversation_id, content, is_read, sent_at) VALUES
(2, 1, 1, 'Is the calculator still available?', FALSE, '2025-11-01 10:00:00'),
(1, 2, 1, 'Yes, still available!', TRUE, '2025-11-01 10:05:00'),
(3, 1, 2, 'Is there any discount possible?', FALSE, '2025-11-02 12:30:00'),
(4, 3, 3, 'Can you share more photos?', FALSE, '2025-11-03 09:15:00');

INSERT INTO FAVOURITE (product_id, user_id) VALUES
(1, 2),
(1, 3),
(4, 1),
(2, 4),
(3, 5);

-- TRANSACTION örnek verisi (product_id benzersizdir; 3 daha önce "sold" olarak girilmiş)
INSERT INTO TRANSACTION (product_id, buyer_id, seller_id, created_at) VALUES
(3, 5, 1, '2025-11-05 10:00:00');

-- TOKEN örnek verileri (bazıları süresi geçmiş, bazıları geçerli)
INSERT INTO TOKEN (user_id, content, type, created_at, expires_at, is_verified) VALUES
(1, 'verif-token-1-abc', 'EMAIL_VERIFICATION', '2025-11-01 08:00:00', '2025-11-10 00:00:00', TRUE),
(2, 'verif-token-2-def', 'EMAIL_VERIFICATION', '2025-11-02 09:00:00', '2025-10-01 00:00:00', FALSE), -- expired
(3, 'password-token-3-ghi', 'PASSWORD_RESET', '2025-11-03 10:00:00', '2026-01-01 00:00:00', TRUE),
(4, 'verif-token-4-jkl', 'EMAIL_VERIFICATION', '2025-11-04 11:00:00', '2025-11-06 00:00:00', FALSE); -- kısa süreli

-- USERS_BLOCK örnek verileri
INSERT INTO USERS_BLOCK (blocker_id, blocked_id, created_at) VALUES
(1, 3, '2025-11-02 13:00:00'),
(2, 5, '2025-11-03 08:00:00');
