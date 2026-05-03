package com.campusflow.presentation.rest.studygroup;

import com.campusflow.application.studygroup.dto.CreateStudyGroupInput;
import com.campusflow.application.studygroup.dto.JoinRequestResult;
import com.campusflow.application.studygroup.dto.StudyGroupResult;
import com.campusflow.application.studygroup.usecase.CreateStudyGroupUseCase;
import com.campusflow.application.studygroup.usecase.GetJoinRequestsUseCase;
import com.campusflow.application.studygroup.usecase.GetStudyGroupsUseCase;
import com.campusflow.application.studygroup.usecase.HandleJoinRequestUseCase;
import com.campusflow.application.studygroup.usecase.RemoveMemberUseCase;
import com.campusflow.application.studygroup.usecase.RequestJoinUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class StudyGroupController {
    private final CreateStudyGroupUseCase createStudyGroupUseCase;
    private final GetStudyGroupsUseCase getStudyGroupsUseCase;
    private final RequestJoinUseCase requestJoinUseCase;
    private final HandleJoinRequestUseCase handleJoinRequestUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final GetJoinRequestsUseCase getJoinRequestsUseCase;

    @PostMapping
    public ResponseEntity<StudyGroupResponse> create(@RequestBody @Valid CreateStudyGroupRequest request) {
        Long userId = extractUserId();
        StudyGroupResult result = createStudyGroupUseCase.create(new CreateStudyGroupInput(
                request.getName(),
                request.getTopic(),
                request.getCourse(),
                request.getCapacity(),
                userId
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<StudyGroupResponse>> getAllOpen() {
        List<StudyGroupResponse> response = getStudyGroupsUseCase.getAllOpen()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(getStudyGroupsUseCase.getById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyGroupResponse>> search(@RequestParam("q") String keyword) {
        List<StudyGroupResponse> response = getStudyGroupsUseCase.search(keyword)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<JoinRequestResponse> requestJoin(@PathVariable Long id) {
        Long userId = extractUserId();
        JoinRequestResult result = requestJoinUseCase.requestJoin(userId, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PutMapping("/join-requests/{requestId}/approve")
    public ResponseEntity<JoinRequestResponse> approve(@PathVariable Long requestId) {
        Long actorId = extractUserId();
        JoinRequestResult result = handleJoinRequestUseCase.approve(requestId, actorId);
        return ResponseEntity.ok(toResponse(result));
    }

    @PutMapping("/join-requests/{requestId}/reject")
    public ResponseEntity<JoinRequestResponse> reject(@PathVariable Long requestId) {
        Long actorId = extractUserId();
        JoinRequestResult result = handleJoinRequestUseCase.reject(requestId, actorId);
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        Long actorId = extractUserId();
        removeMemberUseCase.removeMember(id, memberId, actorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/join-requests")
    public ResponseEntity<List<JoinRequestResponse>> getJoinRequests(@PathVariable Long id) {
        Long actorId = extractUserId();
        List<JoinRequestResponse> response = getJoinRequestsUseCase.getRequestsForGroup(id, actorId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/my-request")
    public ResponseEntity<JoinRequestResponse> getMyRequest(@PathVariable Long id) {
        Long userId = extractUserId();
        JoinRequestResult result = getJoinRequestsUseCase.getMyRequestStatus(userId, id);
        return ResponseEntity.ok(toResponse(result));
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

    private StudyGroupResponse toResponse(StudyGroupResult result) {
        return new StudyGroupResponse(
                result.getId(),
                result.getName(),
                result.getTopic(),
                result.getCourse(),
                result.getCapacity(),
                result.getMemberCount(),
                result.getCreatorId(),
                result.getStatus(),
                result.getCreatedAt()
        );
    }

    private JoinRequestResponse toResponse(JoinRequestResult result) {
        return new JoinRequestResponse(
                result.getId(),
                result.getStudyGroupId(),
                result.getUserId(),
                result.getStatus(),
                result.getRequestedAt()
        );
    }
}
