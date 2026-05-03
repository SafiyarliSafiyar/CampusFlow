package com.campusflow.application.event.service;

import com.campusflow.application.event.usecase.RsvpEventUseCase;
import com.campusflow.domain.event.exception.AlreadyRsvpedException;
import com.campusflow.domain.event.exception.EventCancelledException;
import com.campusflow.domain.event.exception.EventFullException;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.exception.RsvpNotFoundException;
import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.port.EventRepositoryPort;
import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.EmailServicePort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.time.format.DateTimeFormatter;

public class RsvpEventService implements RsvpEventUseCase {
    private final EventRepositoryPort eventRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailServicePort emailServicePort;
    private final NotificationOrchestratorUseCase notificationOrchestratorUseCase;

    public RsvpEventService(
            EventRepositoryPort eventRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            EmailServicePort emailServicePort,
            NotificationOrchestratorUseCase notificationOrchestratorUseCase
    ) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailServicePort = emailServicePort;
        this.notificationOrchestratorUseCase = notificationOrchestratorUseCase;
    }

    @Override
    public void rsvp(Long userId, Long eventId) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.isUpcoming()) {
            throw new EventCancelledException();
        }
        if (!event.hasCapacity()) {
            throw new EventFullException();
        }
        if (eventRepositoryPort.existsRsvp(userId, eventId)) {
            throw new AlreadyRsvpedException();
        }

        eventRepositoryPort.saveRsvp(userId, eventId);

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        notificationOrchestratorUseCase.notifyRsvpConfirmation(userId, eventId, event.getTitle());
        emailServicePort.sendRsvpConfirmationEmail(
                user.getEmail(),
                event.getTitle(),
                event.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                event.getLocation()
        );

        Event updated = Event.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .capacity(event.getCapacity())
                .rsvpCount(event.getRsvpCount() + 1)
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
        eventRepositoryPort.save(updated);
    }

    @Override
    public void cancelRsvp(Long userId, Long eventId) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!eventRepositoryPort.existsRsvp(userId, eventId)) {
            throw new RsvpNotFoundException();
        }

        eventRepositoryPort.deleteRsvp(userId, eventId);
        int nextCount = Math.max(0, event.getRsvpCount() - 1);
        Event updated = Event.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .capacity(event.getCapacity())
                .rsvpCount(nextCount)
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
        eventRepositoryPort.save(updated);
    }
}
