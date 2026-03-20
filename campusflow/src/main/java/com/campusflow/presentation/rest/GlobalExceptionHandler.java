package com.campusflow.presentation.rest;

import com.campusflow.domain.event.exception.AlreadyRsvpedException;
import com.campusflow.domain.event.exception.EventCancelledException;
import com.campusflow.domain.event.exception.EventFullException;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.exception.RsvpNotFoundException;
import com.campusflow.domain.studygroup.exception.AlreadyMemberException;
import com.campusflow.domain.studygroup.exception.AlreadyRequestedException;
import com.campusflow.domain.studygroup.exception.JoinRequestNotFoundException;
import com.campusflow.domain.studygroup.exception.MemberNotFoundException;
import com.campusflow.domain.studygroup.exception.NotGroupCreatorException;
import com.campusflow.domain.studygroup.exception.StudyGroupFullException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.user.exception.EmailAlreadyExistsException;
import com.campusflow.domain.user.exception.InvalidCredentialsException;
import com.campusflow.domain.user.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEventNotFound(EventNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<Map<String, Object>> handleEventFull(EventFullException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EventCancelledException.class)
    public ResponseEntity<Map<String, Object>> handleEventCancelled(EventCancelledException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlreadyRsvpedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyRsvped(AlreadyRsvpedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RsvpNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRsvpNotFound(RsvpNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StudyGroupNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudyGroupNotFound(StudyGroupNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StudyGroupFullException.class)
    public ResponseEntity<Map<String, Object>> handleStudyGroupFull(StudyGroupFullException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlreadyMemberException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyMember(AlreadyMemberException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlreadyRequestedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyRequested(AlreadyRequestedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(JoinRequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJoinRequestNotFound(JoinRequestNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotGroupCreatorException.class)
    public ResponseEntity<Map<String, Object>> handleNotGroupCreator(NotGroupCreatorException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMemberNotFound(MemberNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null
                                ? "Invalid value"
                                : fieldError.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Validation failed");
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
