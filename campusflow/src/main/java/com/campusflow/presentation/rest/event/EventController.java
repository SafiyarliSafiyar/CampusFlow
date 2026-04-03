package com.campusflow.presentation.rest.event;

import com.campusflow.application.event.dto.CreateEventInput;
import com.campusflow.application.event.dto.EventResult;
import com.campusflow.application.event.dto.UpdateEventInput;
import com.campusflow.application.event.usecase.CancelEventUseCase;
import com.campusflow.application.event.usecase.CreateEventUseCase;
import com.campusflow.application.event.usecase.GetEventsUseCase;
import com.campusflow.application.event.usecase.RsvpEventUseCase;
import com.campusflow.application.event.usecase.UpdateEventUseCase;
import com.campusflow.domain.event.exception.EventNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final CreateEventUseCase createEventUseCase;
    private final UpdateEventUseCase updateEventUseCase;
    private final CancelEventUseCase cancelEventUseCase;
    private final GetEventsUseCase getEventsUseCase;
    private final RsvpEventUseCase rsvpEventUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<EventResponse> create(@RequestBody @Valid CreateEventRequest request) {
        Long organizerId = extractUserId();
        EventResult result = createEventUseCase.create(new CreateEventInput(
                request.getTitle(),
                request.getDescription(),
                request.getEventDate(),
                request.getLocation(),
                request.getCapacity(),
                organizerId
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateEventRequest request) {
        EventResult result = updateEventUseCase.update(id, new UpdateEventInput(
                request.getTitle(),
                request.getDescription(),
                request.getEventDate(),
                request.getLocation(),
                request.getCapacity()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        cancelEventUseCase.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getUpcoming() {
        List<EventResult> events = isModeratorOrAdmin()
                ? getEventsUseCase.getAllIncludingCancelled()
                : getEventsUseCase.getUpcoming();

        List<EventResponse> response = events
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> search(@RequestParam("q") String keyword) {
        List<EventResult> events = isModeratorOrAdmin()
                ? getEventsUseCase.searchAll(keyword)
                : getEventsUseCase.search(keyword);

        List<EventResponse> response = events
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        EventResult event = getEventsUseCase.getById(id);
        if (event.getStatus().equals("CANCELLED") && !isModeratorOrAdmin()) {
            throw new EventNotFoundException(id);
        }
        return ResponseEntity.ok(toResponse(event));
    }

    @PostMapping("/{id}/rsvp")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> rsvp(@PathVariable Long id) {
        Long userId = extractUserId();
        rsvpEventUseCase.rsvp(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/rsvp")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancelRsvp(@PathVariable Long id) {
        Long userId = extractUserId();
        rsvpEventUseCase.cancelRsvp(userId, id);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException(
                "User id is missing from authentication context");
    }

    private boolean isModeratorOrAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR")
                        || a.getAuthority().equals("ROLE_ADMIN"));
    }

    private EventResponse toResponse(EventResult result) {
        return new EventResponse(
                result.getId(),
                result.getTitle(),
                result.getDescription(),
                result.getEventDate(),
                result.getLocation(),
                result.getCapacity(),
                result.getRsvpCount(),
                result.getOrganizerId(),
                result.getStatus(),
                result.getCreatedAt()
        );
    }
}
