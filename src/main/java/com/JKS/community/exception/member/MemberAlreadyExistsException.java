package com.JKS.community.exception.member;

public class MemberAlreadyExistsException extends MemberException {

    public MemberAlreadyExistsException() {
        super();
    }

    public MemberAlreadyExistsException(String message) {
        super(message);
    }

    public MemberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}