package com.eldarmamedov.unibank.exception;

public class SameAccountException extends RuntimeException {

    public SameAccountException() {
        super("You are sending money to the same account.");
    }

}
