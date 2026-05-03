package com.campusflow.domain.studygroup.exception;

public class StudyGroupNotFoundException extends RuntimeException {
    public StudyGroupNotFoundException(Long id) {
        super("Study group not found with id: " + id);
    }
}
