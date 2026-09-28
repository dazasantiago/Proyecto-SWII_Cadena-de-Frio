CREATE TABLE envios (
    id VARCHAR(255) PRIMARY KEY,
    temperatura_minima DOUBLE PRECISION NOT NULL,
    temperatura_maxima DOUBLE PRECISION NOT NULL,
    humedad_minima DOUBLE PRECISION NOT NULL,
    humedad_maxima DOUBLE PRECISION NOT NULL,
    estado VARCHAR(255) NOT NULL
);
