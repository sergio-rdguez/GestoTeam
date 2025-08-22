-- Migración V4: Añadir asistencia a entrenamientos y campo de observaciones

-- Añadir campo de observaciones a la tabla training
ALTER TABLE training ADD COLUMN observations TEXT;

-- Añadir campo team_id a la tabla training
ALTER TABLE training ADD COLUMN team_id BIGINT NOT NULL DEFAULT 1;

-- Crear tabla de asistencia a entrenamientos
CREATE TABLE training_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    training_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (training_id) REFERENCES training(id),
    FOREIGN KEY (player_id) REFERENCES player(id)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_training_attendance_training_id ON training_attendance(training_id);
CREATE INDEX idx_training_attendance_player_id ON training_attendance(player_id);
CREATE INDEX idx_training_attendance_status ON training_attendance(status);

-- Crear índice para el campo team_id en training
CREATE INDEX idx_training_team_id ON training(team_id);

-- Añadir restricción única para evitar duplicados de asistencia
ALTER TABLE training_attendance ADD CONSTRAINT uk_training_player UNIQUE (training_id, player_id);
