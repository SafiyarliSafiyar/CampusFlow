package com.campusflow.domain.studygroup.exception;

public class StudyGroupFullException extends RuntimeException {
    public StudyGroupFullException() {
        super("Study group is at full capacity");
    }
}
