package com.campusflow.infrastructure.config;

import com.campusflow.application.event.service.CancelEventService;
import com.campusflow.application.event.service.CreateEventService;
import com.campusflow.application.event.service.GetEventsService;
import com.campusflow.application.event.service.RsvpEventService;
import com.campusflow.application.event.service.UpdateEventService;
import com.campusflow.application.event.usecase.CancelEventUseCase;
import com.campusflow.application.event.usecase.CreateEventUseCase;
import com.campusflow.application.event.usecase.GetEventsUseCase;
import com.campusflow.application.event.usecase.RsvpEventUseCase;
import com.campusflow.application.event.usecase.UpdateEventUseCase;
import com.campusflow.application.studygroup.service.CreateStudyGroupService;
import com.campusflow.application.studygroup.service.GetJoinRequestsService;
import com.campusflow.application.studygroup.service.GetStudyGroupsService;
import com.campusflow.application.studygroup.service.HandleJoinRequestService;
import com.campusflow.application.studygroup.service.RemoveMemberService;
import com.campusflow.application.studygroup.service.RequestJoinService;
import com.campusflow.application.studygroup.usecase.CreateStudyGroupUseCase;
import com.campusflow.application.studygroup.usecase.GetJoinRequestsUseCase;
import com.campusflow.application.studygroup.usecase.GetStudyGroupsUseCase;
import com.campusflow.application.studygroup.usecase.HandleJoinRequestUseCase;
import com.campusflow.application.studygroup.usecase.RemoveMemberUseCase;
import com.campusflow.application.studygroup.usecase.RequestJoinUseCase;
import com.campusflow.application.user.service.AssignRoleService;
import com.campusflow.application.user.service.LoginUserService;
import com.campusflow.application.user.service.RegisterUserService;
import com.campusflow.application.user.service.UpdateProfileService;
import com.campusflow.application.user.usecase.AssignRoleUseCase;
import com.campusflow.application.user.usecase.LoginUserUseCase;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.application.user.usecase.UpdateProfileUseCase;
import com.campusflow.domain.event.port.EventRepositoryPort;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder
    ) {
        return new RegisterUserService(userRepositoryPort, passwordEncoder);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            TokenProviderPort tokenProviderPort
    ) {
        return new LoginUserService(userRepositoryPort, passwordEncoder, tokenProviderPort);
    }

    @Bean
    public UpdateProfileUseCase updateProfileUseCase(UserRepositoryPort userRepositoryPort) {
        return new UpdateProfileService(userRepositoryPort);
    }

    @Bean
    public AssignRoleUseCase assignRoleUseCase(UserRepositoryPort userRepositoryPort) {
        return new AssignRoleService(userRepositoryPort);
    }

    @Bean
    public CreateEventUseCase createEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new CreateEventService(eventRepositoryPort);
    }

    @Bean
    public UpdateEventUseCase updateEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new UpdateEventService(eventRepositoryPort);
    }

    @Bean
    public CancelEventUseCase cancelEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new CancelEventService(eventRepositoryPort);
    }

    @Bean
    public GetEventsUseCase getEventsUseCase(EventRepositoryPort eventRepositoryPort) {
        return new GetEventsService(eventRepositoryPort);
    }

    @Bean
    public RsvpEventUseCase rsvpEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new RsvpEventService(eventRepositoryPort);
    }

    @Bean
    public CreateStudyGroupUseCase createStudyGroupUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new CreateStudyGroupService(studyGroupRepositoryPort);
    }

    @Bean
    public GetStudyGroupsUseCase getStudyGroupsUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new GetStudyGroupsService(studyGroupRepositoryPort);
    }

    @Bean
    public RequestJoinUseCase requestJoinUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new RequestJoinService(studyGroupRepositoryPort);
    }

    @Bean
    public HandleJoinRequestUseCase handleJoinRequestUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new HandleJoinRequestService(studyGroupRepositoryPort);
    }

    @Bean
    public RemoveMemberUseCase removeMemberUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new RemoveMemberService(studyGroupRepositoryPort);
    }

    @Bean
    public GetJoinRequestsUseCase getJoinRequestsUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new GetJoinRequestsService(studyGroupRepositoryPort);
    }
}
