-- Crear la base de datos para ratings
DROP DATABASE IF EXISTS ratingsmicro_db;
CREATE DATABASE ratingsmicro_db;
USE ratingsmicro_db;

-- Crear la tabla para las calificaciones
CREATE TABLE ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    movie_id VARCHAR(50) NOT NULL,
    rating INT CHECK (rating >= 0 AND rating <= 10)
);

-- Insertar datos de ejemplo
INSERT INTO ratings (user_id, movie_id, rating) 
VALUES 
('USER1', 'MOV1', 9),
('USER1', 'MOV2', 8),
('USER2', 'MOV3', 10);
