INSERT INTO UNIVERSITY (name, domain, city) VALUES
('Istanbul Technical University (ITU)', 'itu.edu.tr', 'Istanbul'),
('Middle East Technical University (METU)', 'metu.edu.tr', 'Ankara'),
('Yildiz Technical University (YTU)', 'ytu.edu.tr', 'Istanbul'),
('Bogazici University', 'boun.edu.tr', 'Istanbul'),
('Hacettepe University', 'hacettepe.edu.tr', 'Ankara');

INSERT INTO USERS (full_name, profile_pic_url, email, password_hash, university_id, phone_number, created_at, is_active)
VALUES
('Ayşe Yılmaz', 'https://pics.com/p1.jpg', 'ayse@itu.edu.tr', 'hash123', 1, '5551112233', NOW(), TRUE),
('Mehmet Demir', 'https://pics.com/p2.jpg', 'mehmet@metu.edu.tr', 'hash456', 2, '5554445566', NOW(), TRUE),
('Elif Acar', NULL, 'elif@ytu.edu.tr', 'hash789', 3, '5558889977', NOW(), TRUE),
('Can Koç', 'https://pics.com/p4.jpg', 'can@boun.edu.tr', 'hash101', 4, '5550909090', NOW(), TRUE),
('Zeynep Er', NULL, 'zeynep@hacettepe.edu.tr', 'hash202', 5, '5553030303', NOW(), FALSE);

INSERT INTO PRODUCT (title, description, price, category, status, created_at, user_id, updated_at) VALUES
('Casio Scientific Calculator', 'Used but works perfectly.', 200, 'ELECTRONICS', 'AVAILABLE', NOW(), 1, NOW()),
('Desk Lamp', 'LED desk lamp with adjustable arm.', 150, 'HOME', 'AVAILABLE', NOW(), 2, NOW()),
('Computer Chair', 'Ergonomic chair (blue color)', 700, 'Furniture', 'SOLD', NOW(), 1, NOW()),
('Mechanical Keyboard', 'Red switches, RGB lights.', 850, 'ELECTRONICS', 'AVAILABLE', NOW(), 3, NOW()),
('Graphing Notebook Set', '5 notebooks, A4 size.', 60, 'Stationery', 'AVAILABLE', NOW(), 4, NOW());

INSERT INTO IMAGE (product_id, image_url) VALUES
(1, 'https://pics.com/calculator1.jpg'),
(1, 'https://pics.com/calculator2.jpg'),
(2, 'https://pics.com/lamp1.jpg'),
(3, 'https://pics.com/chair1.jpg'),
(4, 'https://pics.com/keyboard1.jpg');

INSERT INTO MESSAGE (sender_id, receiver_id, product_id, content, is_read, sent_at) VALUES
(2, 1, 1, 'Is the calculator still available?', FALSE, '2025-11-01 10:00:00'),
(1, 2, 1, 'Yes, still available!', TRUE, '2025-11-01 10:05:00'),
(3, 1, 3, 'Is there any discount possible?', FALSE, '2025-11-02 12:30:00'),
(4, 3, 4, 'Can you share more photos?', FALSE, '2025-11-03 09:15:00');

INSERT INTO FAVOURITE (product_id, user_id) VALUES
(1, 2),
(1, 3),
(4, 1),
(2, 4),
(3, 5);
