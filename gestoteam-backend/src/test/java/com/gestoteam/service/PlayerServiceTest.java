package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.enums.Foot;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.*;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.repository.UserSettingsRepository;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.util.GlobalUtil;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMatchStatsRepository playerMatchStatsRepository;
    @Mock
    private UserSettingsRepository userSettingsRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GlobalUtil globalUtil;
    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlayerService playerService;

    private static final Long USER_ID = 1L;
    private Team testTeam;
    private Player testPlayer;
    private PlayerRequest testPlayerRequest;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        // Mock del usuario para BaseService
        User testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setOwnerId(USER_ID);
        testTeam.setName("Test Team");

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("Test Player");
        testPlayer.setSurnameFirst("Doe");
        testPlayer.setSurnameSecond("Smith");
        testPlayer.setPosition(Position.DC);
        testPlayer.setFoot(Foot.DIESTRO);
        testPlayer.setNumber(10);
        testPlayer.setStatus(PlayerStatus.ACTIVO);
        testPlayer.setBirthDate(java.time.LocalDate.of(1995, 5, 15));
        testPlayer.setTeam(testTeam);
        testPlayer.setDeleted(false);
        testPlayer.setCreatedAt(java.time.LocalDateTime.now());
        testPlayer.setUpdatedAt(java.time.LocalDateTime.now());

        testPlayerRequest = new PlayerRequest();
        testPlayerRequest.setName("New Player");
        testPlayerRequest.setSurnameFirst("New");
        testPlayerRequest.setSurnameSecond("Player");
        testPlayerRequest.setPosition(Position.DC);
        testPlayerRequest.setFoot(Foot.DIESTRO);
        testPlayerRequest.setNumber(11);
        testPlayerRequest.setStatus(PlayerStatus.ACTIVO);
        testPlayerRequest.setBirthDate(java.time.LocalDate.of(1996, 6, 16));
        testPlayerRequest.setTeamId(1L);
    }

    @Test
    void getPlayerById_ShouldReturnPlayer_WhenExistsAndBelongsToUser() {
        PlayerResponse playerResponse = new PlayerResponse();
        playerResponse.setId(1L);
        playerResponse.setName("Test Player");

        Season currentSeason = new Season();
        currentSeason.setId(1L);

        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testPlayer));
        when(globalUtil.getCurrentSeason()).thenReturn(currentSeason);
        when(modelMapper.map(any(Player.class), eq(PlayerResponse.class))).thenReturn(playerResponse);

        Optional<PlayerResponse> result = playerService.getPlayerById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(playerRepository).findByIdAndDeletedFalse(1L);
        verify(playerMatchStatsRepository).findByPlayerIdAndMatch_Season_Id(1L, 1L);
    }

    @Test
    void getPlayerById_ShouldReturnEmpty_WhenPlayerDoesNotBelongToUser() {
        testTeam.setOwnerId(2L);
        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testPlayer));

        Optional<PlayerResponse> result = playerService.getPlayerById(1L);

        assertThat(result).isNotPresent();
        verify(playerRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void createPlayer_ShouldSavePlayer_WhenTeamHasCapacity() {
        UserSettings settings = new UserSettings();
        settings.setMaxPlayersPerTeam(25);

        Player savedPlayer = new Player();
        savedPlayer.setId(1L);

        when(userSettingsRepository.findByUserId(USER_ID)).thenReturn(Optional.of(settings));
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTeam));
        when(playerRepository.countByTeamIdAndDeletedFalse(1L)).thenReturn(10L);
        when(modelMapper.map(any(PlayerRequest.class), eq(Player.class))).thenReturn(new Player());
        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);

        Long playerId = playerService.createPlayer(testPlayerRequest);

        assertThat(playerId).isEqualTo(1L);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_ShouldReturnCorrectPlayerId_WhenPlayerIsSaved() {
        UserSettings settings = new UserSettings();
        settings.setMaxPlayersPerTeam(25);

        Player savedPlayer = new Player();
        savedPlayer.setId(99L); // ID diferente para el test

        when(userSettingsRepository.findByUserId(USER_ID)).thenReturn(Optional.of(settings));
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTeam));
        when(playerRepository.countByTeamIdAndDeletedFalse(1L)).thenReturn(5L);
        when(modelMapper.map(any(PlayerRequest.class), eq(Player.class))).thenReturn(new Player());
        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);

        Long playerId = playerService.createPlayer(testPlayerRequest);

        assertThat(playerId).isEqualTo(99L);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_ShouldThrowException_WhenTeamIsFull() {
        UserSettings settings = new UserSettings();
        settings.setMaxPlayersPerTeam(10);

        when(userSettingsRepository.findByUserId(USER_ID)).thenReturn(Optional.of(settings));
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTeam));
        when(playerRepository.countByTeamIdAndDeletedFalse(1L)).thenReturn(10L);

        assertThatThrownBy(() -> playerService.createPlayer(testPlayerRequest))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("No se puede crear más jugadores. Límite alcanzado.");

        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void updatePlayer_ShouldUpdatePlayer_WhenPlayerAndTeamExist() {
        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testPlayer));
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTeam));

        playerService.updatePlayer(1L, testPlayerRequest);

        verify(playerRepository).save(testPlayer);
        verify(modelMapper).map(testPlayerRequest, testPlayer);
    }

    @Test
    void updatePlayer_ShouldThrowException_WhenPlayerNotFound() {
        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updatePlayer(1L, testPlayerRequest))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Jugador no encontrado o no tienes permisos para acceder a él.");
    }

    @Test
    void deletePlayer_ShouldMarkPlayerAsDeleted_WhenPlayerExists() {
        when(playerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testPlayer));

        playerService.deletePlayer(1L);

        verify(playerRepository).save(testPlayer);
        assertThat(testPlayer.getDeleted()).isTrue();
    }

    @Test
    void getPlayersByTeamId_ShouldReturnSummary_WhenTeamExists() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findByTeamIdAndDeletedFalse(1L)).thenReturn(List.of(testPlayer));

        TeamPlayerSummaryResponse.PlayerSummary mappedSummary = new TeamPlayerSummaryResponse.PlayerSummary();
        mappedSummary.setId(1L);
        mappedSummary.setFullName("Test Player");
        mappedSummary.setPhotoUrl("/api/files/test-photo.jpg");
        mappedSummary.setNumber(10);
        mappedSummary.setPosition("DC");
        mappedSummary.setPositionOrder(Position.DC.getOrder());
        mappedSummary.setFoot("Diestro");
        mappedSummary.setStatus(PlayerStatus.ACTIVO);
        when(modelMapper.map(any(Player.class), eq(TeamPlayerSummaryResponse.PlayerSummary.class))).thenReturn(mappedSummary);

        TeamPlayerSummaryResponse response = playerService.getPlayersByTeamId(1L);

        assertThat(response.getTeamId()).isEqualTo(1L);
        assertThat(response.getTeamName()).isEqualTo("Test Team");
        assertThat(response.getTotalPlayers()).isEqualTo(1);
        assertThat(response.getPlayers()).hasSize(1);
    }

    @Test
    void getPlayersByTeamId_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayersByTeamId(1L))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Equipo no encontrado o no tienes permisos para acceder a él.");
    }
}