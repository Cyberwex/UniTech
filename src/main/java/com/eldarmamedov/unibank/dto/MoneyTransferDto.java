package com.eldarmamedov.unibank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MoneyTransferDto {
    private String senderAccNumber;
    private String receiverAccNumber;
    private Long amount;
}
