package com.campusflow.infrastructure.persistence.postgres.event;

import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.model.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class EventJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String location;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int rsvpCount;

    @Column(nullable = false)
    private Long organizerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Event toDomain() {
        return Event.builder()
                .id(id)
                .title(title)
                .description(description)
                .eventDate(eventDate)
                .location(location)
                .latitude(latitude)
                .longitude(longitude)
                .capacity(capacity)
                .rsvpCount(rsvpCount)
                .organizerId(organizerId)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    public static EventJpaEntity fromDomain(Event event) {
        EventJpaEntity entity = new EventJpaEntity();
        entity.setId(event.getId());
        entity.setTitle(event.getTitle());
        entity.setDescription(event.getDescription());
        entity.setEventDate(event.getEventDate());
        entity.setLocation(event.getLocation());
        entity.setLatitude(event.getLatitude());
        entity.setLongitude(event.getLongitude());
        entity.setCapacity(event.getCapacity());
        entity.setRsvpCount(event.getRsvpCount());
        entity.setOrganizerId(event.getOrganizerId());
        entity.setStatus(event.getStatus());
        entity.setCreatedAt(event.getCreatedAt());
        return entity;
    }
}
