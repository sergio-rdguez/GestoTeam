package com.gestoteam.service;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Team;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.TeamRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpponentServiceTest {

    @Mock
    private OpponentRepository opponentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private OpponentService opponentService;

    private static final String USERNAME = "testuser";
    private Team testTeam;
    private Opponent testOpponent;
    private OpponentRequest opponentRequest;
    private OpponentResponse opponentResponse;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setOwnerId(USERNAME);

        opponentRequest = new OpponentRequest();
        opponentRequest.setName("Rival Test");
        opponentRequest.setTeamId(1L);

        testOpponent = new Opponent();
        testOpponent.setId(1L);
        testOpponent.setName("Rival Test");
        testOpponent.setTeam(testTeam);

        opponentResponse = new OpponentResponse();
        opponentResponse.setId(1L);
        opponentResponse.setName("Rival Test");
    }

    @Test
    void createOpponent_ShouldSaveOpponent_WhenTeamExists() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.of(testTeam));
        when(modelMapper.map(opponentRequest, Opponent.class)).thenReturn(testOpponent);
        when(opponentRepository.save(any(Opponent.class))).thenReturn(testOpponent);
        when(modelMapper.map(testOpponent, OpponentResponse.class)).thenReturn(opponentResponse);

        OpponentResponse result = opponentService.createOpponent(opponentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rival Test");
        verify(opponentRepository).save(any(Opponent.class));
    }

    @Test
    void createOpponent_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> opponentService.createOpponent(opponentRequest))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Equipo no encontrado o no tienes permisos para acceder a él.");
    }

    @Test
    void getOpponentsByTeam_ShouldReturnOpponentList_WhenTeamExists() {
        when(teamRepository.existsByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(true);
        when(opponentRepository.findByTeamId(1L)).thenReturn(List.of(testOpponent));
        when(modelMapper.map(testOpponent, OpponentResponse.class)).thenReturn(opponentResponse);

        List<OpponentResponse> result = opponentService.getOpponentsByTeam(1L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rival Test");
    }

    @Test
    void getOpponentsByTeam_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.existsByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(false);

        assertThatThrownBy(() -> opponentService.getOpponentsByTeam(1L))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Equipo no encontrado o no tienes permisos para acceder a él.");
    }
}