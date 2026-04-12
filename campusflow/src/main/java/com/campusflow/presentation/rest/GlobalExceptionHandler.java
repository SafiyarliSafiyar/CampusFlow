package com.campusflow.presentation.rest;

import com.campusflow.domain.event.exception.AlreadyRsvpedException;
import com.campusflow.domain.event.exception.EventCancelledException;
import com.campusflow.domain.event.exception.EventFullException;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.exception.RsvpNotFoundException;
import com.campusflow.domain.message.exception.EmptyMessageException;
import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.post.exception.PostNotFoundException;
import com.campusflow.domain.studygroup.exception.AlreadyMemberException;
import com.campusflow.domain.studygroup.exception.AlreadyRequestedException;
import com.campusflow.domain.studygroup.exception.JoinRequestNotFoundException;
import com.campusflow.domain.studygroup.exception.MemberNotFoundException;
import com.campusflow.domain.studygroup.exception.NotGroupCreatorException;
import com.campusflow.domain.studygroup.exception.StudyGroupFullException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.user.exception.EmailAlreadyExistsException;
import com.campusflow.domain.user.exception.EmailNotVerifiedException;
import com.campusflow.domain.user.exception.InvalidCredentialsException;
import com.campusflow.domain.user.exception.InvalidOtpException;
import com.campusflow.domain.user.exception.OtpNotFoundException;
import com.campusflow.domain.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotVerified(
            EmailNotVerifiedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOtp(
            InvalidOtpException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOtpNotFound(
            OtpNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEventNotFound(
            EventNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<Map<String, Object>> handleEventFull(
            EventFullException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(EventCancelledException.class)
    public ResponseEntity<Map<String, Object>> handleEventCancelled(
            EventCancelledException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AlreadyRsvpedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyRsvped(
            AlreadyRsvpedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(RsvpNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRsvpNotFound(
            RsvpNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(StudyGroupNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudyGroupNotFound(
            StudyGroupNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(StudyGroupFullException.class)
    public ResponseEntity<Map<String, Object>> handleStudyGroupFull(
            StudyGroupFullException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AlreadyMemberException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyMember(
            AlreadyMemberException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AlreadyRequestedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyRequested(
            AlreadyRequestedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(JoinRequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJoinRequestNotFound(
            JoinRequestNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NotGroupCreatorException.class)
    public ResponseEntity<Map<String, Object>> handleNotGroupCreator(
            NotGroupCreatorException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMemberNotFound(
            MemberNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MessageAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleMessageAccessDenied(
            MessageAccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(EmptyMessageException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyMessage(
            EmptyMessageException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePostNotFound(
            PostNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " +
                        (fieldError.getDefaultMessage() == null
                                ? "Invalid value"
                                : fieldError.getDefaultMessage()))
                .orElse("Validation failed");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
