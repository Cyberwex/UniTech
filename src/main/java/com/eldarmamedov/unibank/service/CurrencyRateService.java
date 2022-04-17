package com.eldarmamedov.unibank.service;

import com.eldarmamedov.unibank.dto.CurrencyRateDto;
import com.eldarmamedov.unibank.entity.CurrencyRate;
import com.eldarmamedov.unibank.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyRateDto findCurrencyRateByFromCurrAndToCurr(String fromCurr, String toCurr) {
        CurrencyRate currencyRate = currencyRateRepository.findCurrencyRateByFromCurrAndToCurr(fromCurr.toUpperCase(), toCurr.toUpperCase());
        CurrencyRateDto currencyRateDto = new CurrencyRateDto();

        currencyRateDto.setId(currencyRate.getId());
        currencyRateDto.setFromCurr(currencyRate.getFromCurr());
        currencyRateDto.setToCurr(currencyRate.getToCurr());
        currencyRateDto.setRate(currencyRate.getRate());

        return currencyRateDto;
    }
}
