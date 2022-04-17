package com.eldarmamedov.unibank.service;

import com.eldarmamedov.unibank.dto.MoneyTransferDto;
import com.eldarmamedov.unibank.entity.Account;
import com.eldarmamedov.unibank.entity.Currency;
import com.eldarmamedov.unibank.entity.CurrencyRate;
import com.eldarmamedov.unibank.exception.DeactiveAccountException;
import com.eldarmamedov.unibank.exception.NoEnoughMoneyException;
import com.eldarmamedov.unibank.exception.NonExistingAccountException;
import com.eldarmamedov.unibank.exception.SameAccountException;
import com.eldarmamedov.unibank.repository.AccountRepository;
import com.eldarmamedov.unibank.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    AccountService accountService;

    private MoneyTransferDto moneyTransferDto;
    private Account sender;
    private Account receiver;
    private Currency azn;
    private Currency usd;
    private CurrencyRate currencyRate;

    @BeforeEach
    void setUp() {
        moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("11");

        sender = new Account();
        receiver = new Account();

        azn = new Currency();
        azn.setAlphaCode("AZN");

        usd = new Currency();
        usd.setAlphaCode("USD");

        currencyRate = new CurrencyRate();
    }

    @Test
    void find_active_accounts() {
        assertThat(accountRepository.findAccountsByUserId(anyLong())).isInstanceOf(List.class);
        assertThat(accountService.findActiveAccountsByUser(anyLong())).isInstanceOf(List.class);
    }

    @Test
    void sender_has_not_found() {
        moneyTransferDto.setSenderAccNumber("non existing");

        when(accountRepository.findByAccNumber(anyString())).thenReturn(Optional.empty());
        assertThrows(NonExistingAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void receiver_has_not_found() {
        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(new Account()));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.empty());

        assertThrows(NonExistingAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void transfer_validation_no_enough_money() {
        moneyTransferDto.setAmount(101L);

        sender.setBalance(100L);
        receiver.setIsActive(false);

        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));

        assertThrows(NoEnoughMoneyException.class, () -> accountService.sendMoney(moneyTransferDto));
    }


    @Test
    void transfer_validation_same_account() {
        moneyTransferDto.setAmount(10L);

        sender.setBalance(100L);
        sender.setAccNumber("00");

        receiver.setBalance(100L);
        receiver.setAccNumber("00");

        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));

        assertThrows(SameAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void sending_money_with_same_currency() {
        sender.setAccNumber("00");
        sender.setBalance(100L);
        sender.setCurrency(azn);

        receiver.setAccNumber("11");
        receiver.setBalance(1L);
        receiver.setIsActive(true);
        receiver.setCurrency(azn);

        moneyTransferDto.setAmount(1L);

        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));

        accountService.sendMoney(moneyTransferDto);

        assertEquals(2L, receiver.getBalance());
    }

    @Test
    void sending_money_with_different_currency() {
        currencyRate.setFromCurr(usd.getAlphaCode());
        currencyRate.setToCurr(azn.getAlphaCode());
        currencyRate.setRate(1.7D);

        sender.setAccNumber("00");
        sender.setBalance(100L);
        sender.setCurrency(usd);

        receiver.setAccNumber("11");
        receiver.setBalance(100L);
        receiver.setIsActive(true);
        receiver.setCurrency(azn);

        moneyTransferDto.setAmount(10L);

        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));
        when(currencyRateRepository.findCurrencyRateByFromCurrAndToCurr(sender.getCurrency().getAlphaCode(), receiver.getCurrency().getAlphaCode())).thenReturn(currencyRate);

        accountService.sendMoney(moneyTransferDto);

        assertEquals(90L, sender.getBalance());
        assertEquals(117L, receiver.getBalance());
    }

    @Test
    void transfer_validation_deactive_account() {
        moneyTransferDto.setAmount(10L);

        sender.setBalance(100L);
        sender.setAccNumber("00");

        receiver.setAccNumber("01");
        receiver.setIsActive(false);

        when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));

        assertThrows(DeactiveAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }
}