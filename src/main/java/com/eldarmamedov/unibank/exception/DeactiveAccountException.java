package com.eldarmamedov.unibank.exception;

public class DeactiveAccountException extends RuntimeException {

    public DeactiveAccountException() {
        super("Receiver's account is deactivated");
    }
}
