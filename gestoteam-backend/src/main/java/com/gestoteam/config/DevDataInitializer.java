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
    private final OpponentRepository opponentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Iniciando con perfil 'dev'. Poblando base de datos de prueba...");

            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("password"));
            user.setDeleted(false);
            userRepository.save(user);

            UserSettings settings = new UserSettings();
            settings.setUserId("testuser");
            userSettingsRepository.save(settings);

            Season season = new Season();
            season.setName("2024/2025");
            season.setStartDate(LocalDate.of(2024, 9, 1));
            season.setEndDate(LocalDate.of(2025, 8, 31));
            seasonRepository.save(season);

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

            List<Player> playersTeam1 = createPlayersForTeam1(team1);
            createOpponentsAndMatchesForTeam1(team1, season, playersTeam1);

            log.info("Base de datos de prueba poblada con éxito.");
        };
    }

    private List<Player> createPlayersForTeam1(Team team) {
        Player p1 = new Player();
        p1.setName("Alejandro");
        p1.setSurnameFirst("García");
        p1.setSurnameSecond("López");
        p1.setPosition(Position.POR);
        p1.setNumber(1);
        p1.setStatus(PlayerStatus.ACTIVO);
        p1.setBirthDate(LocalDate.of(1995, 3, 12));
        p1.setTeam(team);

        Player p2 = new Player();
        p2.setName("Javier");
        p2.setSurnameFirst("Sánchez");
        p2.setSurnameSecond("Gómez");
        p2.setPosition(Position.DFC);
        p2.setNumber(4);
        p2.setStatus(PlayerStatus.ACTIVO);
        p2.setBirthDate(LocalDate.of(1994, 11, 5));
        p2.setTeam(team);

        Player p3 = new Player();
        p3.setName("David");
        p3.setSurnameFirst("Fernández");
        p3.setSurnameSecond("Pérez");
        p3.setPosition(Position.DFC);
        p3.setNumber(5);
        p3.setStatus(PlayerStatus.ACTIVO);
        p3.setBirthDate(LocalDate.of(1993, 2, 18));
        p3.setTeam(team);

        Player p4 = new Player();
        p4.setName("Adrián");
        p4.setSurnameFirst("Jiménez");
        p4.setSurnameSecond("Ortega");
        p4.setPosition(Position.LTD);
        p4.setNumber(2);
        p4.setStatus(PlayerStatus.ACTIVO);
        p4.setBirthDate(LocalDate.of(1996, 6, 30));
        p4.setTeam(team);

        Player p5 = new Player();
        p5.setName("Mario");
        p5.setSurnameFirst("Moreno");
        p5.setSurnameSecond("Serrano");
        p5.setPosition(Position.LTI);
        p5.setNumber(3);
        p5.setStatus(PlayerStatus.ACTIVO);
        p5.setBirthDate(LocalDate.of(1997, 1, 25));
        p5.setTeam(team);

        Player p6 = new Player();
        p6.setName("Carlos");
        p6.setSurnameFirst("Romero");
        p6.setSurnameSecond("Vázquez");
        p6.setPosition(Position.MCD);
        p6.setNumber(6);
        p6.setStatus(PlayerStatus.LESIONADO);
        p6.setBirthDate(LocalDate.of(1995, 9, 14));
        p6.setTeam(team);

        Player p7 = new Player();
        p7.setName("Pablo");
        p7.setSurnameFirst("Álvarez");
        p7.setSurnameSecond("Castro");
        p7.setPosition(Position.MC);
        p7.setNumber(8);
        p7.setStatus(PlayerStatus.ACTIVO);
        p7.setBirthDate(LocalDate.of(1996, 4, 22));
        p7.setTeam(team);

        Player p8 = new Player();
        p8.setName("Jorge");
        p8.setSurnameFirst("Domínguez");
        p8.setSurnameSecond("Ramos");
        p8.setPosition(Position.MCO);
        p8.setNumber(10);
        p8.setStatus(PlayerStatus.ACTIVO);
        p8.setBirthDate(LocalDate.of(1994, 12, 10));
        p8.setTeam(team);

        Player p9 = new Player();
        p9.setName("Ignacio");
        p9.setSurnameFirst("Vidal");
        p9.setSurnameSecond("Castillo");
        p9.setPosition(Position.ED);
        p9.setNumber(7);
        p9.setStatus(PlayerStatus.SUSPENDIDO);
        p9.setBirthDate(LocalDate.of(1997, 5, 16));
        p9.setTeam(team);

        Player p10 = new Player();
        p10.setName("Álvaro");
        p10.setSurnameFirst("Molina");
        p10.setSurnameSecond("Iglesias");
        p10.setPosition(Position.EI);
        p10.setNumber(11);
        p10.setStatus(PlayerStatus.ACTIVO);
        p10.setBirthDate(LocalDate.of(1998, 10, 3));
        p10.setTeam(team);

        Player p11 = new Player();
        p11.setName("Rubén");
        p11.setSurnameFirst("Ortega");
        p11.setSurnameSecond("Garrido");
        p11.setPosition(Position.DC);
        p11.setNumber(9);
        p11.setStatus(PlayerStatus.ACTIVO);
        p11.setBirthDate(LocalDate.of(1993, 7, 8));
        p11.setTeam(team);

        return playerRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11));
    }

    private void createOpponentsAndMatchesForTeam1(Team team, Season season, List<Player> players) {
        Opponent opponent1 = new Opponent();
        opponent1.setName("CD La Elipa");
        opponent1.setObservations("Equipo correoso, buen contraataque.");
        opponent1.setTeam(team);

        Opponent opponent2 = new Opponent();
        opponent2.setName("Sporting de Hortaleza");
        opponent2.setObservations("Defensa muy sólida.");
        opponent2.setTeam(team);
        opponentRepository.saveAll(List.of(opponent1, opponent2));

        Match match1 = new Match();
        match1.setDate(LocalDateTime.of(2024, 10, 5, 18, 0));
        match1.setOpponent(opponent1);
        match1.setLocation("Campo La Elipa");
        match1.setResult("1-3");
        match1.setWon(true);
        match1.setFinalized(true);
        match1.setTeam(team);
        match1.setSeason(season);
        matchRepository.save(match1);

        Player javier = players.stream().filter(p -> p.getName().equals("Javier")).findFirst().orElseThrow();
        Player david = players.stream().filter(p -> p.getName().equals("David")).findFirst().orElseThrow();

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

        Match match2 = new Match();
        match2.setDate(LocalDateTime.now().plusDays(15));
        match2.setOpponent(opponent2);
        match2.setLocation("Ciudad Deportiva GestoTeam");
        match2.setFinalized(false);
        match2.setTeam(team);
        match2.setSeason(season);
        matchRepository.save(match2);
    }
}