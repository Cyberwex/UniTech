package com.eldarmamedov.unibank.scheduler;

import com.eldarmamedov.unibank.entity.CurrencyRate;
import com.eldarmamedov.unibank.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CurrencyRateScheduler {

    private static final String THIRD_PARTY_URL = "http://localhost:8090/currency-rates";
    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepo;

    @Scheduled(cron = "0 * * * * *")
    public void updateCurrencyRate() {
        System.out.printf("Started scheduling process at %s \n", ZonedDateTime.now());
        currencyRateRepo.saveAll(getCurrencyRates(THIRD_PARTY_URL));
    }

    public List<CurrencyRate> getCurrencyRates(String url) {
        ResponseEntity<CurrencyRate[]> responseEntity = restTemplate.getForEntity(url, CurrencyRate[].class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("");
        }
        CurrencyRate[] currencyRate = responseEntity.getBody();

        if (currencyRate == null || currencyRate.length == 0) {
            throw new RuntimeException("");
        }

        return Arrays.stream(currencyRate)
                .collect(Collectors.toList());
    }
}
