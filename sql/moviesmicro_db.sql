-- Crear la base de datos para movies
CREATE DATABASE moviesmicro_db;
USE moviesmicro_db;

-- Crear la tabla para las películas
CREATE TABLE movies (
    movie_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Insertar datos de ejemplo
INSERT INTO movies (movie_id, name, description) 
VALUES 
('MOV1', 'Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology.'),
('MOV2', 'The Matrix', 'A computer hacker learns about the true nature of reality.'),
('MOV3', 'Interstellar', 'A team of explorers travel through a wormhole in space.');
