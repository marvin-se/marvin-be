CREATE TABLE university (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    domain VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL
);

INSERT INTO university (name, domain, city) VALUES
('Istanbul Technical University (ITU)', 'itu.edu.tr', 'Istanbul'),
('Middle East Technical University (METU)', 'metu.edu.tr', 'Ankara'),
('Yildiz Technical University (YTU)', 'ytu.edu.tr', 'Istanbul');