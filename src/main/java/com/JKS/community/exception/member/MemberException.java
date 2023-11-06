package com.JKS.community.exception.member;

public abstract class MemberException extends RuntimeException {

    public MemberException() {
        super();
    }

    public MemberException(String message) {
        super(message);
    }

    public MemberException(String message, Throwable cause) {
        super(message, cause);
    }
}