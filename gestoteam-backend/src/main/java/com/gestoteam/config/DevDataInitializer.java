package com.gestoteam.config;

import com.gestoteam.enums.Category;
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

            // Equipo Secundario: Gesto Academy
            Team team2 = createTeam(user, "Gesto Academy", Category.JUVENIL, "Primera Juvenil", "Madrid", "Campo Anexo");
            createPlayersForTeam2(team2);

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
        settings.setUserId(username);
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
        team.setOwnerId(user.getUsername());
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
}