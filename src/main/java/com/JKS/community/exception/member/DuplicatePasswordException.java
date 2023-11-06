package com.JKS.community.exception.member;

public class DuplicatePasswordException extends MemberException {

    public DuplicatePasswordException() {
        super();
    }

    public DuplicatePasswordException(String message) {
        super(message);
    }

    public DuplicatePasswordException(String message, Throwable cause) {
        super(message, cause);
    }

}
