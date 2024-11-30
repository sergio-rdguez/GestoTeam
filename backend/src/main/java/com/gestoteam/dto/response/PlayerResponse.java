package com.gestoteam.dto.response;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerResponse {
    private Long id = 0L;
    private String name = "";
    private String surnameFirst = "";
    private String surnameSecond = "";
    private String fullName = "";
    private Position position = Position.NA;
    private int number = 0;
    private int age = 0;
    private LocalDate birthDate = LocalDate.now();
    private PlayerStatus status = PlayerStatus.ACTIVO;
    private TeamResponse team = new TeamResponse();
    private StatsResponse stats = new StatsResponse();

    @Data
    public static class TeamResponse {
        private Long id = 0L;
        private String name = "";
        private String category = "";
    }

    @Data
    public static class StatsResponse {
        private MatchesStats matches = new MatchesStats();
        private GoalsStats goals = new GoalsStats();
        private CardsStats cards = new CardsStats();

        @Data
        public static class MatchesStats {
            private int convoked = 0;
            private int starter = 0;
            private int substitute = 0;
            private int played = 0;
        }

        @Data
        public static class GoalsStats {
            private int total = 0;
            private double average = 0.0;
        }

        @Data
        public static class CardsStats {
            private int yellow = 0;
            private int red = 0;
            private int doubleYellow = 0;
        }
    }
}
