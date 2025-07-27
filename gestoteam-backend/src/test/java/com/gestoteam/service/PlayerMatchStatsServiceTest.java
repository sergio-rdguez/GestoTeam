package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Match;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.model.Team;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerMatchStatsServiceTest {

    @Mock
    private PlayerMatchStatsRepository playerMatchStatsRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlayerMatchStatsService playerMatchStatsService;

    private static final String USERNAME = "testuser";
    private Team testTeam;
    private Player testPlayer;
    private Match testMatch;
    private PlayerMatchStats testStats;
    private PlayerMatchStatsRequest testRequest;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setOwnerId(USERNAME);

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setTeam(testTeam);

        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setTeam(testTeam);

        testStats = new PlayerMatchStats();
        testStats.setId(1L);
        testStats.setPlayer(testPlayer);
        testStats.setMatch(testMatch);

        testRequest = new PlayerMatchStatsRequest();
        testRequest.setPlayerId(1L);
        testRequest.setMatchId(1L);
        testRequest.setMinutesPlayed(90);
    }

    @Test
    void getPlayerMatchStatsById_ShouldReturnStats_WhenAuthorized() {
        when(playerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(testStats));
        when(modelMapper.map(testStats, PlayerMatchStatsResponse.class)).thenReturn(new PlayerMatchStatsResponse());

        PlayerMatchStatsResponse response = playerMatchStatsService.getPlayerMatchStatsById(1L);

        assertThat(response).isNotNull();
    }

    @Test
    void getPlayerMatchStatsById_ShouldThrowException_WhenNotAuthorized() {
        testTeam.setOwnerId("anotherUser");
        when(playerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(testStats));

        assertThatThrownBy(() -> playerMatchStatsService.getPlayerMatchStatsById(1L))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Estadísticas no encontradas o no tienes permisos para acceder a ellas.");
    }

    @Test
    void createPlayerMatchStats_ShouldSaveStats_WhenRequestIsValid() {
        when(matchRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testMatch));
        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testPlayer));
        when(modelMapper.map(testRequest, PlayerMatchStats.class)).thenReturn(new PlayerMatchStats());
        when(playerMatchStatsRepository.save(any(PlayerMatchStats.class))).thenReturn(testStats);
        when(modelMapper.map(testStats, PlayerMatchStatsResponse.class)).thenReturn(new PlayerMatchStatsResponse());

        playerMatchStatsService.createPlayerMatchStats(testRequest);

        verify(playerMatchStatsRepository).save(any(PlayerMatchStats.class));
    }

    @Test
    void updatePlayerMatchStats_ShouldUpdateStats_WhenAuthorized() {
        when(playerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(testStats));
        when(playerMatchStatsRepository.save(any(PlayerMatchStats.class))).thenReturn(testStats);
        when(modelMapper.map(testStats, PlayerMatchStatsResponse.class)).thenReturn(new PlayerMatchStatsResponse());

        playerMatchStatsService.updatePlayerMatchStats(1L, testRequest);

        verify(playerMatchStatsRepository).save(testStats);
        assertThat(testStats.getMinutesPlayed()).isEqualTo(90);
    }
}