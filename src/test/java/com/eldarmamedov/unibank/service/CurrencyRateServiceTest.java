package com.eldarmamedov.unibank.service;

import com.eldarmamedov.unibank.dto.CurrencyRateDto;
import com.eldarmamedov.unibank.entity.CurrencyRate;
import com.eldarmamedov.unibank.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    CurrencyRateService currencyRateService;

    private CurrencyRate currencyRate;
    private CurrencyRateDto dto;

    @BeforeEach
    void setUp() {
        currencyRate = new CurrencyRate();
        currencyRate.setId(1L);
        currencyRate.setFromCurr("USD");
        currencyRate.setToCurr("AZN");
        currencyRate.setRate(1.7D);
    }

    @Test
    void finding_currency_rate_by_two_currs() {
        Mockito.when(currencyRateRepository.findCurrencyRateByFromCurrAndToCurr("USD", "AZN"))
                .thenReturn(currencyRate);

        CurrencyRateDto currencyRateDto = currencyRateService
                .findCurrencyRateByFromCurrAndToCurr(currencyRate.getFromCurr(), currencyRate.getToCurr());

        assertThat(currencyRate.getId()).isEqualTo(currencyRateDto.getId());
    }
}