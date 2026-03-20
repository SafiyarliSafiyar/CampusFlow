package com.campusflow.domain.studygroup.exception;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException() {
        super("User is already a member of this group");
    }
}
