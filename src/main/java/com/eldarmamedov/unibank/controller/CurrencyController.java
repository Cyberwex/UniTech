package com.eldarmamedov.unibank.controller;

import com.eldarmamedov.unibank.dto.CurrencyRateDto;
import com.eldarmamedov.unibank.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyRateService currencyRateService;

    @GetMapping("/currency-rate/from/{fromCurr}/to/{toCurr}")
    public CurrencyRateDto getCurrencyRate(@PathVariable String fromCurr, @PathVariable String toCurr) {
        return currencyRateService.findCurrencyRateByFromCurrAndToCurr(fromCurr, toCurr);
    }
}
