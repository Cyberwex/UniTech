package com.eldarmamedov.unibank.exception;

public class NonExistingAccountException extends RuntimeException {

    public NonExistingAccountException(String account) {
        super(String.format("There is no account with number %s account.", account));
    }

}
