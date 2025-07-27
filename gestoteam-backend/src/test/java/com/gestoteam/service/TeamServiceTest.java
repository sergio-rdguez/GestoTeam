package com.gestoteam.service;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.enums.Category;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Team;
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
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeamService teamService;

    private static final String USERNAME = "testuser";
    private Team testTeam;
    private TeamRequest testTeamRequest;
    private TeamResponse testTeamResponse;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");
        testTeam.setOwnerId(USERNAME);
        testTeam.setCategory(Category.SENIOR);
        testTeam.setDeleted(false);

        testTeamRequest = new TeamRequest();
        testTeamRequest.setName("Updated Team");
        testTeamRequest.setCategory(Category.SENIOR.name());

        testTeamResponse = new TeamResponse();
        testTeamResponse.setId(1L);
        testTeamResponse.setName("Test Team");
        testTeamResponse.setCategory(Category.SENIOR.getName());
    }

    @Test
    void getAllTeams_ShouldReturnTeamList_WhenTeamsExistForUser() {
        when(teamRepository.findByOwnerIdAndDeletedFalse(USERNAME)).thenReturn(List.of(testTeam));

        List<TeamResponse> result = teamService.getAllTeams();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Team");
        assertThat(result.get(0).getCategory()).isEqualTo(Category.SENIOR.getName());
        verify(teamRepository).findByOwnerIdAndDeletedFalse(USERNAME);
    }

    @Test
    void getTeamById_ShouldReturnTeam_WhenTeamExistsAndBelongsToUser() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.of(testTeam));

        Optional<TeamResponse> result = teamService.getTeamById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getTeamById_ShouldReturnEmpty_WhenTeamNotFound() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.empty());

        Optional<TeamResponse> result = teamService.getTeamById(1L);

        assertThat(result).isNotPresent();
    }

    @Test
    void createTeam_ShouldSaveAndReturnTeam() {
        when(modelMapper.map(testTeamRequest, Team.class)).thenReturn(testTeam);
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);
        when(modelMapper.map(testTeam, TeamResponse.class)).thenReturn(testTeamResponse);

        TeamResponse result = teamService.createTeam(testTeamRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Team");
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void updateTeam_ShouldUpdateTeam_WhenTeamExists() {
        // Configuramos el comportamiento para la primera llamada a map (void)
        doNothing().when(modelMapper).map(eq(testTeamRequest), eq(testTeam));

        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // Configuramos el comportamiento para la segunda llamada a map (con retorno)
        when(modelMapper.map(testTeam, TeamResponse.class)).thenReturn(testTeamResponse);

        TeamResponse result = teamService.updateTeam(1L, testTeamRequest);

        assertThat(result).isNotNull();
        verify(modelMapper).map(testTeamRequest, testTeam); // Verificamos la primera llamada
        verify(teamRepository).save(testTeam);
        verify(modelMapper).map(testTeam, TeamResponse.class); // Verificamos la segunda llamada
    }

    @Test
    void updateTeam_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.updateTeam(1L, testTeamRequest))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Equipo no encontrado o no tienes permisos para acceder a él.");
    }

    @Test
    void deleteTeam_ShouldMarkTeamAsDeleted_WhenTeamExists() {
        when(teamRepository.findByIdAndOwnerId(1L, USERNAME)).thenReturn(Optional.of(testTeam));

        teamService.deleteTeam(1L);

        verify(teamRepository).save(testTeam);
        assertThat(testTeam.getDeleted()).isTrue();
    }

    @Test
    void deleteTeam_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.findByIdAndOwnerId(1L, USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.deleteTeam(1L))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Equipo no encontrado o no tienes permisos para acceder a él.");
    }
}