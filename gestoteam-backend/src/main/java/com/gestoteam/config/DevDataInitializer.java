package com.gestoteam.config;

import com.gestoteam.enums.Category;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.enums.AttendanceStatus;
import com.gestoteam.enums.Foot;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import com.gestoteam.model.*;
import com.gestoteam.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataInitializer {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final MatchRepository matchRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final OpponentRepository opponentRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingAttendanceRepository trainingAttendanceRepository;
    private final ExerciseRepository exerciseRepository;
    private final TableConfigurationRepository tableConfigurationRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Iniciando con perfil 'dev'. Poblando base de datos con datos reales...");

            User user = createUser("testuser", "password");
            Season season = createSeason("2024/2025");

            // Equipo Principal: Atlético Gesto
            Team team1 = createTeam(user, "Atlético Gesto", Category.SENIOR, "Primera Aficionados", "Madrid", "Ciudad Deportiva GestoTeam");
            Map<String, Player> playersTeam1 = createPlayersForTeam1(team1);
            List<Opponent> opponents = createOpponentsForTeam(team1);
            createMatchesForTeam1(team1, season, playersTeam1, opponents);
            
            // Crear ejercicios y entrenamientos
            List<Exercise> exercises = createExercises(user);
            createTrainingsForTeam1(team1, user, playersTeam1, exercises);

            // Equipo Secundario: Gesto Academy
            Team team2 = createTeam(user, "Gesto Academy", Category.JUVENIL, "Primera Juvenil", "Madrid", "Campo Anexo");
            createPlayersForTeam2(team2);

            // Crear configuraciones de tabla por defecto
            createDefaultTableConfigurations(user);

            log.info("Base de datos de prueba poblada con éxito para el usuario '{}'", user.getUsername());
        };
    }

    private User createUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return userRepository.findByUsername(username).get();
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDeleted(false);
        user = userRepository.save(user);

        UserSettings settings = new UserSettings();
        settings.setUserId(user.getId());
        userSettingsRepository.save(settings);
        log.info("Usuario de prueba creado: {}", username);
        return user;
    }

    private Season createSeason(String name) {
        return seasonRepository.findByName(name).orElseGet(() -> {
            Season season = new Season();
            season.setName(name);
            season.setStartDate(LocalDate.of(2024, 9, 1));
            season.setEndDate(LocalDate.of(2025, 8, 31));
            log.info("Temporada creada: {}", name);
            return seasonRepository.save(season);
        });
    }

    private Team createTeam(User user, String name, Category category, String division, String location, String field) {
        Team team = new Team();
        team.setName(name);
        team.setCategory(category);
        team.setDivision(division);
        team.setLocation(location);
        team.setField(field);
        team.setOwnerId(user.getId());
        log.info("Creando equipo: {}", name);
        return teamRepository.save(team);
    }

    private Map<String, Player> createPlayersForTeam1(Team team) {
        List<Player> players = Arrays.asList(
                createPlayer(team, "Sergio", "Ramos", "", Position.DFC, 4, PlayerStatus.ACTIVO, LocalDate.of(1986, 3, 30)),
                createPlayer(team, "Iker", "Casillas", "", Position.POR, 1, PlayerStatus.ACTIVO, LocalDate.of(1981, 5, 20)),
                createPlayer(team, "Andrés", "Iniesta", "", Position.MC, 8, PlayerStatus.ACTIVO, LocalDate.of(1984, 5, 11)),
                createPlayer(team, "David", "Villa", "", Position.DC, 7, PlayerStatus.ACTIVO, LocalDate.of(1981, 12, 3)),
                createPlayer(team, "Xavi", "Hernández", "", Position.MC, 6, PlayerStatus.LESIONADO, LocalDate.of(1980, 1, 25)),
                createPlayer(team, "Carles", "Puyol", "", Position.DFC, 5, PlayerStatus.ACTIVO, LocalDate.of(1978, 4, 13)),
                createPlayer(team, "Fernando", "Torres", "", Position.DC, 9, PlayerStatus.SUSPENDIDO, LocalDate.of(1984, 3, 20)),
                createPlayer(team, "Cesc", "Fàbregas", "", Position.MCO, 10, PlayerStatus.ACTIVO, LocalDate.of(1987, 5, 4)),
                createPlayer(team, "Jordi", "Alba", "", Position.LTI, 18, PlayerStatus.ACTIVO, LocalDate.of(1989, 3, 21)),
                createPlayer(team, "Gerard", "Piqué", "", Position.DFC, 3, PlayerStatus.ACTIVO, LocalDate.of(1987, 2, 2)),
                createPlayer(team, "David", "Silva", "", Position.EI, 21, PlayerStatus.ACTIVO, LocalDate.of(1986, 1, 8))
        );
        playerRepository.saveAll(players);
        log.info("Creados {} jugadores para el equipo {}", players.size(), team.getName());
        // CORRECCIÓN: Usar una clave única para el mapa (nombre + apellido)
        return players.stream().collect(Collectors.toMap(
                player -> player.getName() + " " + player.getSurnameFirst(),
                Function.identity()
        ));
    }

    private void createPlayersForTeam2(Team team) {
        List<Player> players = Arrays.asList(
                createPlayer(team, "Ansu", "Fati", "", Position.ED, 22, PlayerStatus.ACTIVO, LocalDate.of(2002, 10, 31)),
                createPlayer(team, "Pedri", "González", "", Position.MC, 16, PlayerStatus.ACTIVO, LocalDate.of(2002, 11, 25)),
                createPlayer(team, "Gavi", "Páez", "", Position.MC, 30, PlayerStatus.ACTIVO, LocalDate.of(2004, 8, 5))
        );
        playerRepository.saveAll(players);
        log.info("Creados {} jugadores para el equipo {}", players.size(), team.getName());
    }

    private Player createPlayer(Team team, String name, String surname1, String surname2, Position pos, int num, PlayerStatus status, LocalDate birthdate) {
        Player p = new Player();
        p.setName(name);
        p.setSurnameFirst(surname1);
        p.setSurnameSecond(surname2);
        p.setPosition(pos);
        p.setFoot(Foot.DIESTRO); // Valor por defecto
        p.setNumber(num);
        p.setStatus(status);
        p.setBirthDate(birthdate);
        p.setTeam(team);
        return p;
    }

    private List<Opponent> createOpponentsForTeam(Team team) {
        List<Opponent> opponents = Arrays.asList(
                createOpponent(team, "Real Madrid CF", "Máximo rival, muy peligrosos a la contra."),
                createOpponent(team, "FC Barcelona", "Dominan la posesión, presión alta."),
                createOpponent(team, "Atlético de Madrid", "Defensa muy sólida, equipo muy físico."),
                createOpponent(team, "Sevilla FC", "Fuertes en su estadio, juego por bandas."),
                createOpponent(team, "Real Betis", "Equipo con mucha calidad en el centro del campo.")
        );
        return opponentRepository.saveAll(opponents);
    }

    private Opponent createOpponent(Team team, String name, String observations) {
        Opponent opponent = new Opponent();
        opponent.setName(name);
        opponent.setField("Estadio del rival");
        opponent.setObservations(observations);
        opponent.setTeam(team);
        return opponent;
    }

    private void createMatchesForTeam1(Team team, Season season, Map<String, Player> players, List<Opponent> opponents) {
        // Partido 1: Victoria vs Sevilla FC (finalizado)
        Match match1 = createFinalizedMatch(team, season, opponents.get(3), LocalDateTime.now().minusWeeks(2), "Estadio Ramón Sánchez-Pizjuán", 3, 1);
        createPlayerStats(match1, players.get("David Villa"), true, 90, 2, 1, false, false);
        createPlayerStats(match1, players.get("Andrés Iniesta"), true, 90, 1, 2, false, false);
        createPlayerStats(match1, players.get("Sergio Ramos"), true, 90, 0, 0, true, false);

        // Partido 2: Derrota vs Real Betis (finalizado)
        Match match2 = createFinalizedMatch(team, season, opponents.get(4), LocalDateTime.now().minusWeeks(1), "Estadio Benito Villamarín", 0, 1);
        createPlayerStats(match2, players.get("Iker Casillas"), true, 90, 0, 0, false, false);
        createPlayerStats(match2, players.get("Gerard Piqué"), true, 90, 0, 0, true, false);

        // Partido 3: Empate vs Atlético de Madrid (finalizado)
        Match match3 = createFinalizedMatch(team, season, opponents.get(2), LocalDateTime.now().minusDays(4), "Cívitas Metropolitano", 2, 2);
        createPlayerStats(match3, players.get("David Silva"), true, 90, 1, 0, false, false);
        createPlayerStats(match3, players.get("Cesc Fàbregas"), true, 75, 1, 1, false, false);

        // Partido 4: Próximo partido vs FC Barcelona
        createPendingMatch(team, season, opponents.get(1), LocalDateTime.now().plusDays(6), team.getField());
    }

    private Match createFinalizedMatch(Team team, Season season, Opponent opponent, LocalDateTime date, String location, int goalsFor, int goalsAgainst) {
        Match match = new Match();
        match.setDate(date);
        match.setOpponent(opponent);
        match.setLocation(location);
        match.setTeam(team);
        match.setSeason(season);
        match.setFinalized(true);
        match.setGoalsFor(goalsFor);
        match.setGoalsAgainst(goalsAgainst);
        match.setResult(goalsFor + "-" + goalsAgainst);
        match.setWon(goalsFor > goalsAgainst);
        match.setDraw(goalsFor == goalsAgainst);
        return matchRepository.save(match);
    }

    private Match createPendingMatch(Team team, Season season, Opponent opponent, LocalDateTime date, String location) {
        Match match = new Match();
        match.setDate(date);
        match.setOpponent(opponent);
        match.setLocation(location);
        match.setFinalized(false);
        match.setTeam(team);
        match.setSeason(season);
        return matchRepository.save(match);
    }

    private void createPlayerStats(Match match, Player player, boolean starter, int minutes, int goals, int assists, boolean yellow, boolean red) {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setMatch(match);
        stats.setPlayer(player);
        stats.setCalledUp(true);
        stats.setStarter(starter);
        stats.setMinutesPlayed(minutes);
        stats.setGoals(goals);
        stats.setAssists(assists);
        stats.setYellowCard(yellow);
        stats.setRedCard(red);
        playerMatchStatsRepository.save(stats);
    }

    private List<Exercise> createExercises(User user) {
        List<Exercise> exercises = Arrays.asList(
                createExercise(user, "Posesión 4v2", ExerciseCategory.POSESION,
                        "Ejercicio de posesión en espacio reducido con superioridad numérica",
                        "Mantener posesión, pasar y moverse", "Control y pase bajo presión", "Resistencia anaeróbica",
                        "8 conos, 1 balón"),
                        
                createExercise(user, "Finalización desde banda", ExerciseCategory.FINALIZACION,
                        "Centros desde banda y finalización en área",
                        "Centros precisos y finalización", "Técnica de centro y remate", "Velocidad de ejecución",
                        "4 conos, 6 balones, 2 porterías"),
                        
                createExercise(user, "Pressing defensivo", ExerciseCategory.PRESSING,
                        "Ejercicio de presión colectiva tras pérdida",
                        "Presión inmediata, recuperación rápida", "Técnica defensiva", "Resistencia y velocidad",
                        "Conos para delimitar zonas"),
                        
                createExercise(user, "Rondo 7v2", ExerciseCategory.TECNICO,
                        "Posesión en círculo con dos defensores",
                        "Conservar balón, cambios de ritmo", "Pase a primer toque", "Concentración mental",
                        "8 conos en círculo, 1 balón"),
                        
                createExercise(user, "Transiciones ofensivas", ExerciseCategory.TRANSICION,
                        "Cambio rápido de defensa a ataque",
                        "Velocidad en transición", "Pase largo y control", "Velocidad y resistencia",
                        "Conos, 3 balones, 2 porterías"),
                        
                createExercise(user, "Calentamiento dinámico", ExerciseCategory.CALENTAMIENTO,
                        "Activación progresiva antes del entrenamiento",
                        "Preparación física", "Coordinación y movilidad", "Activación muscular progresiva",
                        "Conos, vallas, cuerdas"),
                        
                createExercise(user, "Juego de posición", ExerciseCategory.TACTICO,
                        "Ejercicio de construcción desde atrás",
                        "Circulación de balón, amplitud", "Pase entre líneas", "Resistencia aeróbica",
                        "Conos para marcar zonas"),
                        
                createExercise(user, "Duelos 1v1", ExerciseCategory.TECNICO,
                        "Situaciones de uno contra uno en ataque y defensa",
                        "Superioridad individual", "Regate y entrada", "Fuerza y agilidad",
                        "4 conos, 1 balón"),
                        
                createExercise(user, "Penaltis", ExerciseCategory.TECNICO,
                        "Práctica de lanzamientos desde los once metros",
                        "Efectividad en penaltis", "Técnica de golpeo", "Concentración mental",
                        "Balones, portería"),
                        
                createExercise(user, "Partido modificado", ExerciseCategory.PARTIDO_MODIFICADO,
                        "Partido con condiciones especiales (ej: máximo 3 toques)",
                        "Aplicación táctica global", "Técnica bajo presión", "Resistencia específica",
                        "Conos, balones, petos")
        );
        
        exerciseRepository.saveAll(exercises);
        log.info("Creados {} ejercicios para el usuario {}", exercises.size(), user.getUsername());
        return exercises;
    }

    private Exercise createExercise(User user, String title, ExerciseCategory category, String description,
                                  String tacticalObjectives, String technicalObjectives, String physicalObjectives,
                                  String materials) {
        Exercise exercise = new Exercise();
        exercise.setTitle(title);
        exercise.setCategory(category);
        exercise.setDescription(description);
        exercise.setTacticalObjectives(tacticalObjectives);
        exercise.setTechnicalObjectives(technicalObjectives);
        exercise.setPhysicalObjectives(physicalObjectives);
        exercise.setMaterials(materials);
        exercise.setUser(user);
        exercise.setDeleted(false);
        return exercise;
    }

    private void createTrainingsForTeam1(Team team, User user, Map<String, Player> players, List<Exercise> exercises) {
        // Entrenamiento pasado - Táctico
        Training training1 = createTraining(user, team, LocalDateTime.now().minusDays(5),
                "Ciudad Deportiva GestoTeam", "Táctico",
                "Trabajo de posesión y pressing. Equipo muy concentrado.");
        addExercisesToTraining(training1, Arrays.asList(exercises.get(0), exercises.get(2), exercises.get(6)));
        createAttendanceForTraining(training1, players);

        // Entrenamiento pasado - Técnico
        Training training2 = createTraining(user, team, LocalDateTime.now().minusDays(3),
                "Ciudad Deportiva GestoTeam", "Técnico",
                "Sesión de finalización y técnica individual.");
        addExercisesToTraining(training2, Arrays.asList(exercises.get(1), exercises.get(3), exercises.get(7)));
        createAttendanceForTraining(training2, players);

        // Entrenamiento de hoy
        Training training3 = createTraining(user, team, LocalDateTime.now().plusHours(2),
                "Ciudad Deportiva GestoTeam", "Físico",
                "Trabajo de resistencia y fuerza. Preparación para el próximo partido.");
        addExercisesToTraining(training3, Arrays.asList(exercises.get(5), exercises.get(4), exercises.get(9)));

        // Entrenamiento futuro
        Training training4 = createTraining(user, team, LocalDateTime.now().plusDays(2),
                "Ciudad Deportiva GestoTeam", "Táctico",
                "Preparación específica para el rival del domingo.");
        addExercisesToTraining(training4, Arrays.asList(exercises.get(6), exercises.get(2), exercises.get(8)));

        log.info("Creados 4 entrenamientos para el equipo {}", team.getName());
    }

    private Training createTraining(User user, Team team, LocalDateTime date, String location,
                                  String trainingType, String observations) {
        Training training = new Training();
        training.setDate(date);
        training.setLocation(location);
        training.setTrainingType(trainingType);
        training.setObservations(observations);
        training.setUser(user);
        training.setTeam(team);
        training.setDeleted(false);
        return trainingRepository.save(training);
    }

    private void addExercisesToTraining(Training training, List<Exercise> exercises) {
        training.setExercises(exercises);
        trainingRepository.save(training);
    }

    private void createAttendanceForTraining(Training training, Map<String, Player> players) {
        // Crear asistencia variada para hacer los datos más realistas
        int i = 0;
        for (Player player : players.values()) {
            AttendanceStatus status;
            String notes = "";
            
            // Simular diferentes tipos de asistencia
            switch (i % 6) {
                case 0:
                case 1:
                case 2:
                    status = AttendanceStatus.PRESENT;
                    break;
                case 3:
                    status = AttendanceStatus.LATE;
                    notes = "Llegó 10 minutos tarde por tráfico";
                    break;
                case 4:
                    status = AttendanceStatus.INJURED;
                    notes = "Molestias en el tobillo";
                    break;
                default:
                    status = AttendanceStatus.JUSTIFIED_ABSENCE;
                    notes = "Motivos personales justificados";
            }
            
            createTrainingAttendance(training, player, status, notes);
            i++;
        }
    }

    private void createTrainingAttendance(Training training, Player player, AttendanceStatus status, String notes) {
        TrainingAttendance attendance = new TrainingAttendance();
        attendance.setTraining(training);
        attendance.setPlayer(player);
        attendance.setStatus(status);
        attendance.setNotes(notes);
        attendance.setDeleted(false);
        trainingAttendanceRepository.save(attendance);
    }

    private void createDefaultTableConfigurations(User user) {
        log.info("Creando configuraciones de tabla por defecto para el usuario: {}", user.getUsername());
        
        // Configuración para tabla de jugadores
        TableConfiguration playersConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("players")
            .pageSize(10)
            .defaultSortKey("fullName")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "photoUrl", "Foto", true, false, 1, "80px"));
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "fullName", "Nombre", true, true, 2, "200px"));
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "position", "Posición", true, true, 3, "120px"));
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "foot", "Pie", true, true, 4, "80px"));
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "number", "Dorsal", true, true, 5, "80px"));
        playersConfig.addColumnConfiguration(createTableColumn(playersConfig, "status", "Estado", true, true, 6, "120px"));
        
        tableConfigurationRepository.save(playersConfig);
        
        // Configuración para tabla de equipos
        TableConfiguration teamsConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        teamsConfig.addColumnConfiguration(createTableColumn(teamsConfig, "name", "Nombre", true, true, 1, "200px"));
        teamsConfig.addColumnConfiguration(createTableColumn(teamsConfig, "category", "Categoría", true, true, 2, "120px"));
        teamsConfig.addColumnConfiguration(createTableColumn(teamsConfig, "division", "División", true, true, 3, "120px"));
        teamsConfig.addColumnConfiguration(createTableColumn(teamsConfig, "location", "Ubicación", true, true, 4, "150px"));
        
        tableConfigurationRepository.save(teamsConfig);
        
        // Configuración para tabla de partidos
        TableConfiguration matchesConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("matches")
            .pageSize(10)
            .defaultSortKey("date")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .build();
        
        matchesConfig.addColumnConfiguration(createTableColumn(matchesConfig, "date", "Fecha", true, true, 1, "120px"));
        matchesConfig.addColumnConfiguration(createTableColumn(matchesConfig, "opponent", "Rival", true, true, 2, "150px"));
        matchesConfig.addColumnConfiguration(createTableColumn(matchesConfig, "homeAway", "Local/Visitante", true, true, 3, "120px"));
        matchesConfig.addColumnConfiguration(createTableColumn(matchesConfig, "result", "Resultado", true, false, 4, "100px"));
        
        tableConfigurationRepository.save(matchesConfig);
        
        // Configuración para tabla de rivales
        TableConfiguration opponentsConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("opponents")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        opponentsConfig.addColumnConfiguration(createTableColumn(opponentsConfig, "name", "Nombre", true, true, 1, "200px"));
        opponentsConfig.addColumnConfiguration(createTableColumn(opponentsConfig, "field", "Campo", true, true, 2, "120px"));
        opponentsConfig.addColumnConfiguration(createTableColumn(opponentsConfig, "observations", "Observaciones", true, false, 3, "200px"));
        
        tableConfigurationRepository.save(opponentsConfig);
        
        // Configuración para tabla de estadísticas de jugadores
        TableConfiguration playerStatsConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("player-stats")
            .pageSize(10)
            .defaultSortKey("starter")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .build();
        
        playerStatsConfig.addColumnConfiguration(createTableColumn(playerStatsConfig, "playerFullName", "Jugador", true, true, 1, "200px"));
        playerStatsConfig.addColumnConfiguration(createTableColumn(playerStatsConfig, "minutesPlayed", "Minutos", true, true, 2, "100px"));
        playerStatsConfig.addColumnConfiguration(createTableColumn(playerStatsConfig, "goals", "Goles", true, true, 3, "80px"));
        playerStatsConfig.addColumnConfiguration(createTableColumn(playerStatsConfig, "assists", "Asist.", true, true, 4, "80px"));
        playerStatsConfig.addColumnConfiguration(createTableColumn(playerStatsConfig, "cards", "Tarjetas", true, false, 5, "100px"));
        
        tableConfigurationRepository.save(playerStatsConfig);
        
        // Configuración para tabla de entrenamientos
        TableConfiguration trainingsConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("trainings")
            .pageSize(10)
            .defaultSortKey("date")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .build();
        
        trainingsConfig.addColumnConfiguration(createTableColumn(trainingsConfig, "date", "Fecha", true, true, 1, "120px"));
        trainingsConfig.addColumnConfiguration(createTableColumn(trainingsConfig, "trainingType", "Tipo", true, true, 2, "120px"));
        trainingsConfig.addColumnConfiguration(createTableColumn(trainingsConfig, "location", "Ubicación", true, true, 3, "150px"));
        trainingsConfig.addColumnConfiguration(createTableColumn(trainingsConfig, "observations", "Observaciones", true, false, 4, "200px"));
        
        tableConfigurationRepository.save(trainingsConfig);
        
        // Configuración para tabla de asistencia a entrenamientos
        TableConfiguration attendanceConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("attendance")
            .pageSize(10)
            .defaultSortKey("playerName")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        attendanceConfig.addColumnConfiguration(createTableColumn(attendanceConfig, "playerName", "Jugador", true, true, 1, "200px"));
        attendanceConfig.addColumnConfiguration(createTableColumn(attendanceConfig, "status", "Estado", true, true, 2, "120px"));
        attendanceConfig.addColumnConfiguration(createTableColumn(attendanceConfig, "notes", "Notas", true, false, 3, "200px"));
        
        tableConfigurationRepository.save(attendanceConfig);
        
        // Configuración para tabla de ejercicios
        TableConfiguration exercisesConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("exercises")
            .pageSize(10)
            .defaultSortKey("title")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        exercisesConfig.addColumnConfiguration(createTableColumn(exercisesConfig, "title", "Título", true, true, 1, "200px"));
        exercisesConfig.addColumnConfiguration(createTableColumn(exercisesConfig, "category", "Categoría", true, true, 2, "120px"));
        exercisesConfig.addColumnConfiguration(createTableColumn(exercisesConfig, "description", "Descripción", true, false, 3, "250px"));
        exercisesConfig.addColumnConfiguration(createTableColumn(exercisesConfig, "tacticalObjectives", "Obj. Tácticos", true, false, 4, "150px"));
        
        tableConfigurationRepository.save(exercisesConfig);
        
        log.info("Configuraciones de tabla por defecto creadas para el usuario: {}", user.getUsername());
    }

    private TableColumnConfiguration createTableColumn(TableConfiguration tableConfig, String key, String label, 
                                                     boolean visible, boolean sortable, int order, String width) {
        return TableColumnConfiguration.builder()
            .tableConfiguration(tableConfig)
            .columnKey(key)
            .columnLabel(label)
            .visible(visible)
            .sortable(sortable)
            .sortOrder(order)
            .width(width)
            .isDefault(true)
            .build();
    }
}