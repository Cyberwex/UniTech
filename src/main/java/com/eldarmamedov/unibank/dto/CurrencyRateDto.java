package com.eldarmamedov.unibank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CurrencyRateDto {
    private Long id;
    private String fromCurr;
    private String toCurr;
    private Double rate;
}
