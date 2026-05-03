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
import com.campusflow.application.message.service.GetMessagesService;
import com.campusflow.application.message.service.SendFileMessageService;
import com.campusflow.application.message.service.SendMessageService;
import com.campusflow.application.message.usecase.GetMessagesUseCase;
import com.campusflow.application.message.usecase.SendFileMessageUseCase;
import com.campusflow.application.message.usecase.SendMessageUseCase;
import com.campusflow.application.post.service.CreatePostService;
import com.campusflow.application.post.service.DeletePostService;
import com.campusflow.application.post.service.GetFeedService;
import com.campusflow.application.post.usecase.CreatePostUseCase;
import com.campusflow.application.post.usecase.DeletePostUseCase;
import com.campusflow.application.post.usecase.GetFeedUseCase;
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
import com.campusflow.application.user.service.RequestPasswordResetService;
import com.campusflow.application.user.service.ResetPasswordService;
import com.campusflow.application.user.service.SendOtpService;
import com.campusflow.application.user.service.UpdateProfileService;
import com.campusflow.application.user.service.VerifyOtpService;
import com.campusflow.application.user.service.GetProfileCompletenessService;
import com.campusflow.application.user.usecase.GetProfileCompletenessUseCase;
import com.campusflow.application.audit.service.GetAuditLogService;
import com.campusflow.application.audit.service.GetModerationQueueService;
import com.campusflow.application.audit.service.ReportContentService;
import com.campusflow.application.audit.usecase.GetAuditLogUseCase;
import com.campusflow.application.audit.usecase.GetModerationQueueUseCase;
import com.campusflow.application.audit.usecase.ReportContentUseCase;
import com.campusflow.domain.audit.port.AuditLogRepositoryPort;
import com.campusflow.application.notification.service.GetNotificationsService;
import com.campusflow.application.notification.service.MarkNotificationsReadService;
import com.campusflow.application.notification.service.NotificationOrchestratorService;
import com.campusflow.application.notification.usecase.GetNotificationsUseCase;
import com.campusflow.application.notification.usecase.MarkNotificationsReadUseCase;
import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;
import com.campusflow.application.push.service.RegisterWebPushSubscriptionService;
import com.campusflow.application.push.service.WebPushService;
import com.campusflow.application.push.usecase.RegisterWebPushSubscriptionUseCase;
import com.campusflow.application.push.usecase.SendTestWebPushUseCase;
import com.campusflow.domain.user.port.PushTokenRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.campusflow.infrastructure.storage.LocalUploadsStorage;
import com.campusflow.application.user.usecase.AssignRoleUseCase;
import com.campusflow.application.user.usecase.LoginUserUseCase;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.application.user.usecase.RequestPasswordResetUseCase;
import com.campusflow.application.user.usecase.ResetPasswordUseCase;
import com.campusflow.application.user.usecase.SendOtpUseCase;
import com.campusflow.application.user.usecase.UpdateProfileUseCase;
import com.campusflow.application.user.usecase.VerifyOtpUseCase;
import com.campusflow.domain.event.port.EventRepositoryPort;
import com.campusflow.domain.message.port.MessagePushPort;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.domain.post.port.PostRepositoryPort;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.port.EmailServicePort;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class AppConfig {
    @Value("${campusflow.webpush.subject:mailto:admin@localhost}")
    private String webPushSubject;

    @Value("${campusflow.webpush.public-key:}")
    private String webPushPublicKey;

    @Value("${campusflow.webpush.private-key:}")
    private String webPushPrivateKey;
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            SendOtpUseCase sendOtpUseCase
    ) {
        return new RegisterUserService(userRepositoryPort, passwordEncoder, sendOtpUseCase);
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
    public GetProfileCompletenessUseCase getProfileCompletenessUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetProfileCompletenessService(userRepositoryPort);
    }

    @Bean
    public SendOtpUseCase sendOtpUseCase(
            OtpRepositoryPort otpRepositoryPort,
            EmailServicePort emailServicePort
    ) {
        return new SendOtpService(otpRepositoryPort, emailServicePort);
    }

    @Bean
    public VerifyOtpUseCase verifyOtpUseCase(
            OtpRepositoryPort otpRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        return new VerifyOtpService(otpRepositoryPort, userRepositoryPort);
    }

    @Bean
    public RequestPasswordResetUseCase requestPasswordResetUseCase(
            UserRepositoryPort userRepositoryPort,
            OtpRepositoryPort otpRepositoryPort,
            EmailServicePort emailServicePort
    ) {
        return new RequestPasswordResetService(userRepositoryPort, otpRepositoryPort, emailServicePort);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            OtpRepositoryPort otpRepositoryPort,
            PasswordEncoder passwordEncoder
    ) {
        return new ResetPasswordService(userRepositoryPort, otpRepositoryPort, passwordEncoder);
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
    public RsvpEventUseCase rsvpEventUseCase(
            EventRepositoryPort eventRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            EmailServicePort emailServicePort,
            com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase notificationOrchestratorUseCase
    ) {
        return new RsvpEventService(eventRepositoryPort, userRepositoryPort, emailServicePort, notificationOrchestratorUseCase);
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
    public HandleJoinRequestUseCase handleJoinRequestUseCase(
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase notificationOrchestratorUseCase
    ) {
        return new HandleJoinRequestService(studyGroupRepositoryPort, notificationOrchestratorUseCase);
    }

    @Bean
    public RemoveMemberUseCase removeMemberUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new RemoveMemberService(studyGroupRepositoryPort);
    }

    @Bean
    public GetJoinRequestsUseCase getJoinRequestsUseCase(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        return new GetJoinRequestsService(studyGroupRepositoryPort);
    }

    @Bean
    public SendMessageUseCase sendMessageUseCase(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            MessagePushPort messagePushPort,
            com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase notificationOrchestratorUseCase
    ) {
        return new SendMessageService(messageRepositoryPort, studyGroupRepositoryPort, messagePushPort, notificationOrchestratorUseCase);
    }

    @Bean
    public SendFileMessageUseCase sendFileMessageUseCase(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            MessagePushPort messagePushPort,
            com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase notificationOrchestratorUseCase,
            LocalUploadsStorage localUploadsStorage
    ) {
        return new SendFileMessageService(
                messageRepositoryPort,
                studyGroupRepositoryPort,
                messagePushPort,
                notificationOrchestratorUseCase,
                localUploadsStorage
        );
    }

    @Bean
    public GetMessagesUseCase getMessagesUseCase(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort
    ) {
        return new GetMessagesService(messageRepositoryPort, studyGroupRepositoryPort);
    }

    @Bean
    public CreatePostUseCase createPostUseCase(PostRepositoryPort postRepositoryPort) {
        return new CreatePostService(postRepositoryPort);
    }

    @Bean
    public DeletePostUseCase deletePostUseCase(PostRepositoryPort postRepositoryPort) {
        return new DeletePostService(postRepositoryPort);
    }

    @Bean
    public GetFeedUseCase getFeedUseCase(PostRepositoryPort postRepositoryPort) {
        return new GetFeedService(postRepositoryPort);
    }

    @Bean
    public ReportContentUseCase reportContentUseCase(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new ReportContentService(auditLogRepositoryPort);
    }

    @Bean
    public GetAuditLogUseCase getAuditLogUseCase(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new GetAuditLogService(auditLogRepositoryPort);
    }

    @Bean
    public GetModerationQueueUseCase getModerationQueueUseCase(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new GetModerationQueueService(auditLogRepositoryPort);
    }

    @Bean
    public NotificationOrchestratorUseCase notificationOrchestratorUseCase(
            NotificationRepositoryPort notificationRepositoryPort
    ) {
        return new NotificationOrchestratorService(notificationRepositoryPort);
    }

    @Bean
    public GetNotificationsUseCase getNotificationsUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        return new GetNotificationsService(notificationRepositoryPort);
    }

    @Bean
    public MarkNotificationsReadUseCase markNotificationsReadUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        return new MarkNotificationsReadService(notificationRepositoryPort);
    }

    @Bean
    public RegisterWebPushSubscriptionUseCase registerWebPushSubscriptionUseCase(
            PushTokenRepositoryPort pushTokenRepositoryPort,
            ObjectMapper objectMapper
    ) {
        return new RegisterWebPushSubscriptionService(pushTokenRepositoryPort, objectMapper);
    }

    @Bean
    public SendTestWebPushUseCase sendTestWebPushUseCase(
            PushTokenRepositoryPort pushTokenRepositoryPort,
            ObjectMapper objectMapper
    ) {
        return new WebPushService(pushTokenRepositoryPort, objectMapper, webPushSubject, webPushPublicKey, webPushPrivateKey);
    }
}
