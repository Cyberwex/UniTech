package com.eldarmamedov.unibank.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AccountDto {
    private Long id;
    private String accNumber;
    private Long balance;
    private String currency;
}
