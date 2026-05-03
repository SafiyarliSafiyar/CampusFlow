package com.campusflow.infrastructure.config;

import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.model.EventStatus;
import com.campusflow.domain.event.port.EventRepositoryPort;
import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.domain.post.model.Post;
import com.campusflow.domain.post.model.PostType;
import com.campusflow.domain.post.port.PostRepositoryPort;
import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.model.StudyGroupStatus;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.model.UserRole;
import com.campusflow.domain.user.model.VerificationStatus;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private static final String DEMO_PASSWORD = "CampusFlow123!";

    private final UserRepositoryPort userRepositoryPort;
    private final PostRepositoryPort postRepositoryPort;
    private final EventRepositoryPort eventRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        User admin = ensureUser(
                "Admin Demo",
                "admin.demo@ada.edu.az",
                UserRole.ADMIN
        );
        User moderator = ensureUser(
                "Moderator Demo",
                "moderator.demo@ada.edu.az",
                UserRole.MODERATOR
        );
        User student = ensureUser(
                "Student Demo",
                "student.demo@ada.edu.az",
                UserRole.STUDENT
        );

        if (postRepositoryPort.countAll() == 0) {
            seedPosts(admin, moderator);
        }
        if (eventRepositoryPort.findAll().isEmpty()) {
            seedEvents(admin, moderator, student);
        }
        if (studyGroupRepositoryPort.findAllOpen().isEmpty()) {
            seedStudyGroups(admin, moderator, student);
        }

        log.info(
                "CampusFlow demo accounts ready: admin.demo@ada.edu.az, moderator.demo@ada.edu.az, student.demo@ada.edu.az (password: {})",
                DEMO_PASSWORD
        );
    }

    private User ensureUser(String username, String email, UserRole role) {
        return userRepositoryPort.findByEmail(email).orElseGet(() -> userRepositoryPort.save(
                User.builder()
                        .id(null)
                        .username(username)
                        .email(email)
                        .major(null)
                        .interests(null)
                        .profilePhotoUrl(null)
                        .passwordHash(passwordEncoder.encode(DEMO_PASSWORD))
                        .role(role)
                        .verificationStatus(VerificationStatus.VERIFIED)
                        .createdAt(LocalDateTime.now())
                        .build()
        ));
    }

    private void seedPosts(User admin, User moderator) {
        postRepositoryPort.save(Post.builder()
                .id(null)
                .title("Welcome to CampusFlow")
                .content("This demo feed is seeded so the frontend shows real campus content on first launch.")
                .type(PostType.ANNOUNCEMENT)
                .authorId(admin.getId())
                .authorUsername(admin.getUsername())
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        postRepositoryPort.save(Post.builder()
                .id(null)
                .title("Spring study sprint this Friday")
                .content("Join a focused evening session in the library to prepare for upcoming quizzes together.")
                .type(PostType.EVENT_UPDATE)
                .authorId(moderator.getId())
                .authorUsername(moderator.getUsername())
                .createdAt(LocalDateTime.now().minusDays(1))
                .build());

        postRepositoryPort.save(Post.builder()
                .id(null)
                .title("Community guidelines reminder")
                .content("Be kind, keep posts relevant, and report anything inappropriate so moderators can review it quickly.")
                .type(PostType.GENERAL)
                .authorId(admin.getId())
                .authorUsername(admin.getUsername())
                .createdAt(LocalDateTime.now().minusHours(10))
                .build());
    }

    private void seedEvents(User admin, User moderator, User student) {
        Event productNight = eventRepositoryPort.save(Event.builder()
                .id(null)
                .title("Campus Product Night")
                .description("A casual showcase where students pitch ideas, demo prototypes, and meet collaborators.")
                .eventDate(LocalDateTime.now().plusDays(5))
                .location("ADA Innovation Lab")
                .latitude(null)
                .longitude(null)
                .capacity(80)
                .rsvpCount(0)
                .organizerId(admin.getId())
                .status(EventStatus.UPCOMING)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        Event designClinic = eventRepositoryPort.save(Event.builder()
                .id(null)
                .title("Design Critique Clinic")
                .description("Bring your UI or poster drafts and get structured feedback from peers and moderators.")
                .eventDate(LocalDateTime.now().plusDays(9))
                .location("Student Center Room 204")
                .latitude(null)
                .longitude(null)
                .capacity(40)
                .rsvpCount(0)
                .organizerId(moderator.getId())
                .status(EventStatus.UPCOMING)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        productNight = addRsvp(productNight, student);
        productNight = addRsvp(productNight, moderator);
        addRsvp(designClinic, student);
    }

    private Event addRsvp(Event event, User user) {
        if (!eventRepositoryPort.existsRsvp(user.getId(), event.getId())) {
            eventRepositoryPort.saveRsvp(user.getId(), event.getId());
            Event updated = Event.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .eventDate(event.getEventDate())
                    .location(event.getLocation())
                    .latitude(event.getLatitude())
                    .longitude(event.getLongitude())
                    .capacity(event.getCapacity())
                    .rsvpCount(eventRepositoryPort.countRsvp(event.getId()))
                    .organizerId(event.getOrganizerId())
                    .status(event.getStatus())
                    .createdAt(event.getCreatedAt())
                    .build();
            return eventRepositoryPort.save(updated);
        }
        return event;
    }

    private void seedStudyGroups(User admin, User moderator, User student) {
        StudyGroup algorithms = createGroup(
                "Algorithms Circle",
                "Graph problems, DP practice, and mock interviews.",
                "CS301",
                6,
                student.getId(),
                LocalDateTime.now().minusDays(4)
        );

        StudyGroup designSystems = createGroup(
                "Design Systems Lab",
                "UI critique, component thinking, and React interface polish.",
                "CSCI3612",
                8,
                moderator.getId(),
                LocalDateTime.now().minusDays(3)
        );

        studyGroupRepositoryPort.saveJoinRequest(JoinRequest.builder()
                .id(null)
                .studyGroupId(algorithms.getId())
                .userId(admin.getId())
                .status(JoinRequestStatus.ACCEPTED)
                .requestedAt(LocalDateTime.now().minusDays(2))
                .build());
        studyGroupRepositoryPort.addMember(admin.getId(), algorithms.getId());
        algorithms = studyGroupRepositoryPort.save(copyGroup(algorithms, 2));

        studyGroupRepositoryPort.saveJoinRequest(JoinRequest.builder()
                .id(null)
                .studyGroupId(designSystems.getId())
                .userId(student.getId())
                .status(JoinRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now().minusHours(12))
                .build());

        messageRepositoryPort.save(Message.builder()
                .id(null)
                .studyGroupId(algorithms.getId())
                .senderId(student.getId())
                .senderUsername(student.getUsername())
                .content("Welcome! I started this group for people who want consistent weekly problem-solving sessions.")
                .sentAt(LocalDateTime.now().minusHours(6))
                .build());
        messageRepositoryPort.save(Message.builder()
                .id(null)
                .studyGroupId(algorithms.getId())
                .senderId(admin.getId())
                .senderUsername(admin.getUsername())
                .content("Count me in. I can share a shortlist of graph and dynamic programming questions to begin with.")
                .sentAt(LocalDateTime.now().minusHours(5))
                .build());
        messageRepositoryPort.save(Message.builder()
                .id(null)
                .studyGroupId(designSystems.getId())
                .senderId(moderator.getId())
                .senderUsername(moderator.getUsername())
                .content("This group is for design critique, interface systems, and thoughtful frontend feedback.")
                .sentAt(LocalDateTime.now().minusHours(4))
                .build());
    }

    private StudyGroup createGroup(
            String name,
            String topic,
            String course,
            int capacity,
            Long creatorId,
            LocalDateTime createdAt
    ) {
        StudyGroup saved = studyGroupRepositoryPort.save(StudyGroup.builder()
                .id(null)
                .name(name)
                .topic(topic)
                .course(course)
                .capacity(capacity)
                .memberCount(1)
                .creatorId(creatorId)
                .status(StudyGroupStatus.OPEN)
                .createdAt(createdAt)
                .build());
        studyGroupRepositoryPort.addMember(creatorId, saved.getId());
        return saved;
    }

    private StudyGroup copyGroup(StudyGroup group, int memberCount) {
        return StudyGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .topic(group.getTopic())
                .course(group.getCourse())
                .capacity(group.getCapacity())
                .memberCount(memberCount)
                .creatorId(group.getCreatorId())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
