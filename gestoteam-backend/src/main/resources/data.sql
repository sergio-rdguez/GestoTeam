-- Contraseña para todos los usuarios es 'password' (encriptada con BCrypt)
-- Usuario principal para las pruebas
INSERT INTO users (id, username, password, deleted, created_at, updated_at)
VALUES (1, 'testuser', '$2a$10$GRLdNijSQe8WqU3e.sJ8h.j5S1a.49a42qw1v5U3.3s4T/cjwI7f6', false, NOW(), NOW());

-- Configuración para el usuario principal
INSERT INTO user_settings (id, user_id, max_players_per_team, max_players_per_match, yellow_cards_for_suspension, max_trainings_per_week, notify_before_match, notify_before_training, created_at, updated_at)
VALUES (1, 'testuser', 25, 18, 5, 4, true, true, NOW(), NOW());

-- Temporada actual
INSERT INTO season (id, name, start_date, end_date)
VALUES (1, '2024/2025', '2024-09-01', '2025-08-31');

-- ==== EQUIPO 1: "Atlético Gesto" (Senior) ====
INSERT INTO team (id, name, location, field, division, description, category, owner_id, deleted, created_at, updated_at)
VALUES (1, 'Atlético Gesto', 'Madrid', 'Ciudad Deportiva GestoTeam', 'Primera Aficionados', 'Primer equipo del club', 'SENIOR', 'testuser', false, NOW(), NOW());

-- Jugadores para el equipo 1 (Atlético Gesto)
INSERT INTO player (id, name, surname_first, surname_second, position, number, status, birth_date, team_id, deleted, created_at, updated_at) VALUES
(1, 'Alejandro', 'García', 'López', 'POR', 1, 'ACTIVO', '1995-03-12', 1, false, NOW(), NOW()),
(2, 'Daniel', 'Martínez', 'Ruiz', 'POR', 13, 'ACTIVO', '1998-07-21', 1, false, NOW(), NOW()),
(3, 'Javier', 'Sánchez', 'Gómez', 'DFC', 4, 'ACTIVO', '1994-11-05', 1, false, NOW(), NOW()),
(4, 'David', 'Fernández', 'Pérez', 'DFC', 5, 'ACTIVO', '1993-02-18', 1, false, NOW(), NOW()),
(5, 'Adrián', 'Jiménez', 'Ortega', 'LTD', 2, 'ACTIVO', '1996-06-30', 1, false, NOW(), NOW()),
(6, 'Mario', 'Moreno', 'Serrano', 'LTI', 3, 'ACTIVO', '1997-01-25', 1, false, NOW(), NOW()),
(7, 'Carlos', 'Romero', 'Vázquez', 'MCD', 6, 'LESIONADO', '1995-09-14', 1, false, NOW(), NOW()),
(8, 'Pablo', 'Álvarez', 'Castro', 'MC', 8, 'ACTIVO', '1996-04-22', 1, false, NOW(), NOW()),
(9, 'Sergio', 'Gutiérrez', 'Navarro', 'MC', 21, 'ACTIVO', '1999-08-01', 1, false, NOW(), NOW()),
(10, 'Jorge', 'Domínguez', 'Ramos', 'MCO', 10, 'ACTIVO', '1994-12-10', 1, false, NOW(), NOW()),
(11, 'Ignacio', 'Vidal', 'Castillo', 'ED', 7, 'SUSPENDIDO', '1997-05-16', 1, false, NOW(), NOW()),
(12, 'Álvaro', 'Molina', 'Iglesias', 'EI', 11, 'ACTIVO', '1998-10-03', 1, false, NOW(), NOW()),
(13, 'Rubén', 'Ortega', 'Garrido', 'DC', 9, 'ACTIVO', '1993-07-08', 1, false, NOW(), NOW()),
(14, 'Francisco', 'Marín', 'Santos', 'DFC', 15, 'ACTIVO', '1999-03-28', 1, false, NOW(), NOW()),
(15, 'Marcos', 'Redondo', 'Cano', 'MC', 14, 'ACTIVO', '2000-02-11', 1, false, NOW(), NOW()),
(16, 'Óscar', 'Prieto', 'Gil', 'EI', 17, 'ACTIVO', '1998-11-20', 1, false, NOW(), NOW()),
(17, 'Samuel', 'Crespo', 'León', 'DC', 19, 'ACTIVO', '1997-06-04', 1, false, NOW(), NOW()),
(18, 'Héctor', 'Pascual', 'Vega', 'LTD', 22, 'ACTIVO', '2001-01-09', 1, false, NOW(), NOW()),
(19, 'Guillermo', 'Soria', 'Blanco', 'MCD', 16, 'ACTIVO', '1996-09-19', 1, false, NOW(), NOW()),
(20, 'Luis', 'Herrera', 'Moya', 'MCO', 20, 'ACTIVO', '1995-04-15', 1, false, NOW(), NOW()),
(21, 'Diego', 'Santos', 'Peña', 'DC', 23, 'ACTIVO', '2002-10-30', 1, false, NOW(), NOW()),
(22, 'Víctor', 'Reyes', 'Campos', 'ED', 18, 'ACTIVO', '1999-05-25', 1, false, NOW(), NOW());

-- ==== EQUIPO 2: "Gesto Academy" (Juvenil) ====
INSERT INTO team (id, name, location, field, division, description, category, owner_id, deleted, created_at, updated_at)
VALUES (2, 'Gesto Academy', 'Madrid', 'Campo Anexo', 'Primera Juvenil', 'Equipo promesa para el futuro', 'JUVENIL', 'testuser', false, NOW(), NOW());

-- Jugadores para el equipo 2 (Gesto Academy)
INSERT INTO player (id, name, surname_first, surname_second, position, number, status, birth_date, team_id, deleted, created_at, updated_at) VALUES
(23, 'Javier', 'López', 'García', 'DC', 9, 'ACTIVO', '2006-04-10', 2, false, NOW(), NOW()),
(24, 'Mateo', 'Díaz', 'Pérez', 'POR', 1, 'ACTIVO', '2007-01-20', 2, false, NOW(), NOW());


-- ==== PARTIDOS Y ESTADÍSTICAS PARA EQUIPO 1 ====

-- Partido 1 (Finalizado - Victoria)
INSERT INTO match (id, date, opponent, location, result, deleted, won, finalized, season_id, team_id, created_at, updated_at)
VALUES (1, '2024-10-05 18:00:00', 'CD La Elipa', 'Campo La Elipa', '1-3', false, true, true, 1, 1, NOW(), NOW());

-- Estadísticas Partido 1
INSERT INTO player_match_stats (match_id, player_id, goals, minutes_played, yellow_card, double_yellow_card, red_card, goals_conceded, own_goals, called_up, starter) VALUES
(1, 1, 0, 90, false, false, false, 1, 0, true, true), -- POR
(1, 3, 0, 90, true, false, false, 0, 0, true, true),  -- DFC
(1, 4, 1, 90, false, false, false, 0, 0, true, true),  -- DFC
(1, 5, 0, 90, false, false, false, 0, 0, true, true),  -- LTD
(1, 6, 0, 90, false, false, false, 0, 0, true, true),  -- LTI
(1, 8, 0, 80, false, false, false, 0, 0, true, true),  -- MC
(1, 10, 1, 90, false, false, false, 0, 0, true, true), -- MCO
(1, 11, 0, 75, false, false, false, 0, 0, true, true), -- ED
(1, 12, 0, 90, false, false, false, 0, 0, true, true), -- EI
(1, 13, 1, 90, false, false, false, 0, 0, true, true), -- DC
(1, 15, 0, 10, false, false, false, 0, 0, true, false), -- MC (Suplente)
(1, 16, 0, 15, false, false, false, 0, 0, true, false), -- EI (Suplente)
(1, 2, 0, 0, false, false, false, 0, 0, true, false),   -- POR (Suplente)
(1, 14, 0, 0, false, false, false, 0, 0, true, false),  -- DFC (Suplente)
(1, 17, 0, 0, false, false, false, 0, 0, true, false);  -- DC (Suplente)

-- Partido 2 (Finalizado - Empate)
INSERT INTO match (id, date, opponent, location, result, deleted, won, finalized, season_id, team_id, created_at, updated_at)
VALUES (2, '2024-10-12 16:30:00', 'Sporting de Hortaleza', 'Ciudad Deportiva GestoTeam', '2-2', false, false, true, 1, 1, NOW(), NOW());

-- Estadísticas Partido 2
INSERT INTO player_match_stats (match_id, player_id, goals, minutes_played, yellow_card, double_yellow_card, red_card, goals_conceded, own_goals, called_up, starter) VALUES
(2, 1, 0, 90, false, false, false, 2, 0, true, true),
(2, 3, 0, 90, false, false, false, 0, 0, true, true),
(2, 4, 0, 90, true, false, false, 0, 0, true, true),
(2, 5, 0, 90, false, false, false, 0, 0, true, true),
(2, 6, 0, 45, true, false, false, 0, 0, true, true),
(2, 8, 0, 90, false, false, false, 0, 0, true, true),
(2, 10, 2, 90, false, false, false, 0, 0, true, true),
(2, 12, 0, 90, false, false, false, 0, 0, true, true),
(2, 13, 0, 60, false, false, false, 0, 0, true, true),
(2, 19, 0, 90, false, false, false, 0, 0, true, true),
(2, 17, 0, 30, false, false, false, 0, 0, true, false),
(2, 18, 0, 45, false, false, false, 0, 0, true, false);

-- Partido 3 (Finalizado - Derrota con expulsión)
INSERT INTO match (id, date, opponent, location, result, deleted, won, finalized, season_id, team_id, created_at, updated_at)
VALUES (3, '2024-10-19 20:00:00', 'Vallecas CF', 'Nuestra Señora de la Torre', '3-0', false, false, true, 1, 1, NOW(), NOW());

-- Estadísticas Partido 3
INSERT INTO player_match_stats (match_id, player_id, goals, minutes_played, yellow_card, double_yellow_card, red_card, goals_conceded, own_goals, called_up, starter) VALUES
(3, 2, 0, 90, false, false, false, 3, 0, true, true), -- POR
(3, 3, 0, 70, false, false, true, 0, 0, true, true),  -- DFC (Roja directa)
(3, 4, 0, 90, true, false, false, 0, 0, true, true),  -- DFC
(3, 5, 0, 90, false, false, false, 0, 0, true, true),  -- LTD
(3, 6, 0, 90, true, false, false, 0, 0, true, true),  -- LTI
(3, 8, 0, 90, false, false, false, 0, 0, true, true),  -- MC
(3, 9, 0, 45, false, false, false, 0, 0, true, true),  -- MC
(3, 10, 0, 90, false, false, false, 0, 0, true, true), -- MCO
(3, 12, 0, 65, false, false, false, 0, 0, true, true), -- EI
(3, 13, 0, 90, false, false, false, 0, 0, true, true), -- DC
(3, 22, 0, 90, false, false, false, 0, 0, true, true); -- ED

-- Partido 4 (Futuro - Pendiente)
INSERT INTO match (id, date, opponent, location, result, deleted, won, finalized, season_id, team_id, created_at, updated_at)
VALUES (4, '2025-05-15 19:00:00', 'Rayo Vallecano C', 'Ciudad Deportiva Rayo Vallecano', null, false, false, false, 1, 1, NOW(), NOW());