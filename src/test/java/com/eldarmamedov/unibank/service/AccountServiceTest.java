package com.eldarmamedov.unibank.service;


import com.eldarmamedov.unibank.dto.AccountDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    AccountService accountService;


    @Test
    void find_active_accounts() {
        assertThat(accountRepository.findAccountsByUserId(anyLong())).isInstanceOf(List.class);
        assertThat(accountService.findActiveAccountsByUser(anyLong())).isInstanceOf(List.class);
    }


    @Test
    void sender_has_not_found() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("non existing");

        Mockito.when(accountRepository.findByAccNumber(anyString())).thenReturn(Optional.empty());
        assertThrows(NonExistingAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void receiver_has_not_found() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("existing");
        moneyTransferDto.setReceiverAccNumber("not existing");

        Mockito.when(accountRepository.findByAccNumber("existing")).thenReturn(Optional.of(new Account()));
        Mockito.when(accountRepository.findByAccNumber("not existing")).thenReturn(Optional.empty());

        assertThrows(NonExistingAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void transfer_validation_no_enough_money() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("01");
        moneyTransferDto.setAmount(101L);

        Account sender = new Account();
        sender.setBalance(100L);
        Account receiver = new Account();
        receiver.setIsActive(false);

        Mockito.when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        Mockito.when(accountRepository.findByAccNumber("01")).thenReturn(Optional.of(receiver));

        assertThrows(NoEnoughMoneyException.class, () -> accountService.sendMoney(moneyTransferDto));
    }


    @Test
    void transfer_validation_same_account() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("01");
        moneyTransferDto.setAmount(10L);

        Account sender = new Account();
        sender.setBalance(100L);
        sender.setAccNumber("00");
        Account receiver = new Account();
        receiver.setBalance(100L);
        receiver.setAccNumber("00");

        Mockito.when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        Mockito.when(accountRepository.findByAccNumber("01")).thenReturn(Optional.of(receiver));

        assertThrows(SameAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }

    @Test
    void sending_money_with_same_currency() {
        Currency currency = new Currency();
        currency.setAlphaCode("azn");

        Account sender = new Account();
        sender.setAccNumber("00");
        sender.setBalance(100L);
        sender.setCurrency(currency);

        Account receiver = new Account();
        receiver.setAccNumber("11");
        receiver.setBalance(1L);
        receiver.setIsActive(true);
        receiver.setCurrency(currency);

        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("11");
        moneyTransferDto.setAmount(1L);

        Mockito.when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        Mockito.when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));

        accountService.sendMoney(moneyTransferDto);

        assertEquals(2L, receiver.getBalance());
    }


    @Test
    void sending_money_with_different_currency() {
        Currency azn = new Currency();
        azn.setAlphaCode("AZN");

        Currency usd = new Currency();
        usd.setAlphaCode("USD");

        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setFromCurr(usd.getAlphaCode());
        currencyRate.setToCurr(azn.getAlphaCode());
        currencyRate.setRate(1.7D);

        Account sender = new Account();
        sender.setAccNumber("00");
        sender.setBalance(100L);
        sender.setCurrency(usd);

        Account receiver = new Account();
        receiver.setAccNumber("11");
        receiver.setBalance(100L);
        receiver.setIsActive(true);
        receiver.setCurrency(azn);

        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("11");
        moneyTransferDto.setAmount(10L);

        Mockito.when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        Mockito.when(accountRepository.findByAccNumber("11")).thenReturn(Optional.of(receiver));
        Mockito.when(currencyRateRepository.findCurrencyRateByFromCurrAndToCurr(sender.getCurrency().getAlphaCode(),
                receiver.getCurrency().getAlphaCode())).thenReturn(currencyRate);

        accountService.sendMoney(moneyTransferDto);

        assertEquals(90L, sender.getBalance());
        assertEquals(117L, receiver.getBalance());
    }

    @Test
    void transfer_validation_deactive_account() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccNumber("00");
        moneyTransferDto.setReceiverAccNumber("01");
        moneyTransferDto.setAmount(10L);

        Account sender = new Account();
        sender.setBalance(100L);
        sender.setAccNumber("00");
        Account receiver = new Account();
        receiver.setAccNumber("01");
        receiver.setIsActive(false);

        Mockito.when(accountRepository.findByAccNumber("00")).thenReturn(Optional.of(sender));
        Mockito.when(accountRepository.findByAccNumber("01")).thenReturn(Optional.of(receiver));

        assertThrows(DeactiveAccountException.class, () -> accountService.sendMoney(moneyTransferDto));
    }
}