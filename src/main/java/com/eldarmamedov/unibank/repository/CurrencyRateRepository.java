package com.eldarmamedov.unibank.repository;

import com.eldarmamedov.unibank.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    CurrencyRate findCurrencyRateByFromCurrAndToCurr(String fromCurr, String toCurr);
}
