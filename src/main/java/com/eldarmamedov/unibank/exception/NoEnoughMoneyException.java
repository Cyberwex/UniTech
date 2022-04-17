package com.eldarmamedov.unibank.exception;

import lombok.Getter;

@Getter
public class NoEnoughMoneyException extends RuntimeException {

    public NoEnoughMoneyException(String account) {
        super(String.format("U don't have enough money on your account %s.", account));
    }

}
