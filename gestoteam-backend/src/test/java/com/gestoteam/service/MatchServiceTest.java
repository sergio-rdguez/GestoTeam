package com.gestoteam.service;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.*;
import com.gestoteam.repository.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private OpponentRepository opponentRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GlobalUtil globalUtil;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private MatchService matchService;

    private static final String USERNAME = "testuser";
    private Team testTeam;
    private Opponent testOpponent;
    private Season testSeason;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setOwnerId(USERNAME);

        testOpponent = new Opponent();
        testOpponent.setId(1L);
        testOpponent.setName("Rival Test");

        testSeason = new Season();
        testSeason.setId(1L);

        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setTeam(testTeam);
        testMatch.setOpponent(testOpponent);
        testMatch.setSeason(testSeason);
        testMatch.setDate(LocalDateTime.now().plusDays(7));
    }

    @Test
    void createMatch_ShouldSaveMatch_WhenRequestIsValid() {
        MatchRequest request = new MatchRequest();
        request.setTeamId(1L);
        request.setOpponentId(1L);
        request.setDate(LocalDateTime.now().plusDays(10));
        request.setLocation("Estadio Test");

        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.of(testTeam));
        when(opponentRepository.findById(1L)).thenReturn(Optional.of(testOpponent));
        when(globalUtil.getCurrentSeason()).thenReturn(testSeason);
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);
        when(modelMapper.map(any(Match.class), eq(MatchResponse.class))).thenReturn(new MatchResponse());

        matchService.createMatch(request);

        verify(matchRepository).save(any(Match.class));
    }

    //TODO: Arreglar este est
    /*@Test
    void getMatchesByTeam_ShouldReturnMatches_WhenTeamExists() {
        when(teamRepository.existsByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(true);
        when(globalUtil.getCurrentSeason()).thenReturn(testSeason);
        when(matchRepository.findByTeamIdAndSeason_IdAndDeletedFalse(1L, 1L)).thenReturn(List.of(testMatch));

        List<MatchResponse> result = matchService.getMatchesByTeam(1L);

        assertThat(result).isNull();
    }*/

    @Test
    void updateMatch_ShouldUpdate_WhenMatchExistsAndBelongsToUser() {
        MatchUpdateRequest request = new MatchUpdateRequest();
        request.setOpponentId(1L);
        request.setLocation("Nuevo Estadio");
        request.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        when(matchRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testMatch));
        when(opponentRepository.findById(1L)).thenReturn(Optional.of(testOpponent));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);
        when(modelMapper.map(any(Match.class), eq(MatchResponse.class))).thenReturn(new MatchResponse());

        matchService.updateMatch(1L, request);

        verify(matchRepository).save(testMatch);
        assertThat(testMatch.getLocation()).isEqualTo("Nuevo Estadio");
    }

    @Test
    void deleteMatch_ShouldMarkAsDeleted_WhenMatchExists() {
        when(matchRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testMatch));

        matchService.deleteMatch(1L);

        verify(matchRepository).save(testMatch);
        assertThat(testMatch.isDeleted()).isTrue();
    }

    @Test
    void deleteMatch_ShouldThrowException_WhenMatchDoesNotBelongToUser() {
        testTeam.setOwnerId("anotherUser");
        when(matchRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testMatch));

        assertThatThrownBy(() -> matchService.deleteMatch(1L))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Partido no encontrado o no tienes permisos para acceder a él.");
    }

    @Test
    void getMatchDetailsById_ShouldReturnDetails_WhenMatchExists() {
        when(matchRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testMatch));
        when(playerRepository.findByTeamIdAndDeletedFalse(1L)).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(Match.class), eq(MatchDetailsResponse.class))).thenReturn(new MatchDetailsResponse());

        MatchDetailsResponse response = matchService.getMatchDetailsById(1L);

        assertThat(response).isNotNull();
        verify(matchRepository).findByIdAndDeletedFalse(1L);
    }
}