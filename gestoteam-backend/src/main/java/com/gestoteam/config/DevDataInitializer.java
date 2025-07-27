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
import java.util.List;

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
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Iniciando con perfil 'dev'. Poblando base de datos de prueba...");

            // 1. Crear Usuario y Configuración
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("password"));
            user.setDeleted(false);
            userRepository.save(user);

            UserSettings settings = new UserSettings();
            settings.setUserId("testuser");
            userSettingsRepository.save(settings);

            // 2. Crear Temporada
            Season season = new Season();
            season.setName("2024/2025");
            season.setStartDate(LocalDate.of(2024, 9, 1));
            season.setEndDate(LocalDate.of(2025, 8, 31));
            seasonRepository.save(season);
            
            // 3. Crear Equipos
            Team team1 = new Team();
            team1.setName("Atlético Gesto");
            team1.setCategory(Category.SENIOR);
            team1.setDivision("Primera Aficionados");
            team1.setLocation("Madrid");
            team1.setField("Ciudad Deportiva GestoTeam");
            team1.setOwnerId("testuser");
            teamRepository.save(team1);

            Team team2 = new Team();
            team2.setName("Gesto Academy");
            team2.setCategory(Category.JUVENIL);
            team2.setDivision("Primera Juvenil");
            team2.setLocation("Madrid");
            team2.setField("Campo Anexo");
            team2.setOwnerId("testuser");
            teamRepository.save(team2);

            // 4. Crear Jugadores para el Equipo 1
            createPlayersForTeam1(team1);
            
            // 5. Crear Partidos y Estadísticas para el Equipo 1
            createMatchesAndStatsForTeam1(team1, season);

            log.info("Base de datos de prueba poblada con éxito.");
        };
    }

    private void createPlayersForTeam1(Team team) {
        playerRepository.saveAll(List.of(
            new Player(null, "Alejandro", "García", "López", "", Position.POR, 1, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1995, 3, 12), team, null),
            new Player(null, "Javier", "Sánchez", "Gómez", "", Position.DFC, 4, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1994, 11, 5), team, null),
            new Player(null, "David", "Fernández", "Pérez", "", Position.DFC, 5, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1993, 2, 18), team, null),
            new Player(null, "Adrián", "Jiménez", "Ortega", "", Position.LTD, 2, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1996, 6, 30), team, null),
            new Player(null, "Mario", "Moreno", "Serrano", "", Position.LTI, 3, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1997, 1, 25), team, null),
            new Player(null, "Carlos", "Romero", "Vázquez", "", Position.MCD, 6, 0, PlayerStatus.LESIONADO, null, null, false, LocalDate.of(1995, 9, 14), team, null),
            new Player(null, "Pablo", "Álvarez", "Castro", "", Position.MC, 8, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1996, 4, 22), team, null),
            new Player(null, "Jorge", "Domínguez", "Ramos", "", Position.MCO, 10, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1994, 12, 10), team, null),
            new Player(null, "Ignacio", "Vidal", "Castillo", "", Position.ED, 7, 0, PlayerStatus.SUSPENDIDO, null, null, false, LocalDate.of(1997, 5, 16), team, null),
            new Player(null, "Álvaro", "Molina", "Iglesias", "", Position.EI, 11, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1998, 10, 3), team, null),
            new Player(null, "Rubén", "Ortega", "Garrido", "", Position.DC, 9, 0, PlayerStatus.ACTIVO, null, null, false, LocalDate.of(1993, 7, 8), team, null)
        ));
    }

    private void createMatchesAndStatsForTeam1(Team team, Season season) {
        // Partido 1 (Finalizado)
        Match match1 = new Match();
        match1.setDate(LocalDateTime.of(2024, 10, 5, 18, 0));
        match1.setOpponent("CD La Elipa");
        match1.setLocation("Campo La Elipa");
        match1.setResult("1-3");
        match1.setWon(true);
        match1.setFinalized(true);
        match1.setTeam(team);
        match1.setSeason(season);
        matchRepository.save(match1);

        // Estadísticas Partido 1
        Player javier = playerRepository.findByName("Javier").get(0);
        Player david = playerRepository.findByName("David").get(0);
        
        PlayerMatchStats stats1 = new PlayerMatchStats();
        stats1.setMatch(match1);
        stats1.setPlayer(javier);
        stats1.setMinutesPlayed(90);
        stats1.setYellowCard(true);
        stats1.setCalledUp(true);
        stats1.setStarter(true);
        playerMatchStatsRepository.save(stats1);

        PlayerMatchStats stats2 = new PlayerMatchStats();
        stats2.setMatch(match1);
        stats2.setPlayer(david);
        stats2.setGoals(1);
        stats2.setMinutesPlayed(90);
        stats2.setCalledUp(true);
        stats2.setStarter(true);
        playerMatchStatsRepository.save(stats2);

        // Partido 2 (Futuro)
        Match match2 = new Match();
        match2.setDate(LocalDateTime.now().plusDays(15));
        match2.setOpponent("Sporting de Hortaleza");
        match2.setLocation("Ciudad Deportiva GestoTeam");
        match2.setFinalized(false);
        match2.setTeam(team);
        match2.setSeason(season);
        matchRepository.save(match2);
    }
}