package com.eldarmamedov.unibank.service;

import com.eldarmamedov.unibank.dto.AccountDto;
import com.eldarmamedov.unibank.dto.MoneyTransferDto;
import com.eldarmamedov.unibank.entity.Account;
import com.eldarmamedov.unibank.entity.CurrencyRate;
import com.eldarmamedov.unibank.exception.DeactiveAccountException;
import com.eldarmamedov.unibank.exception.NoEnoughMoneyException;
import com.eldarmamedov.unibank.exception.NonExistingAccountException;
import com.eldarmamedov.unibank.exception.SameAccountException;
import com.eldarmamedov.unibank.repository.AccountRepository;
import com.eldarmamedov.unibank.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public List<AccountDto> findActiveAccountsByUser(Long id) {
        return accountRepository.findAccountsByUserId(id)
                .stream()
                .filter(Account::getIsActive)
                .map(this::toAccountDto)
                .collect(Collectors.toList());
    }

    private AccountDto toAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setAccNumber(account.getAccNumber());
        accountDto.setBalance(account.getBalance());
        accountDto.setCurrency(account.getCurrency().getAlphaCode());
        return accountDto;
    }


    @Transactional
    public void sendMoney(MoneyTransferDto moneyTransferDto) {
        Account senderAccount = accountRepository.findByAccNumber(moneyTransferDto.getSenderAccNumber())
                .orElseThrow(() -> new NonExistingAccountException(moneyTransferDto.getSenderAccNumber()));
        Account receiverAccount = accountRepository.findByAccNumber(moneyTransferDto.getReceiverAccNumber())
                .orElseThrow(() -> new NonExistingAccountException(moneyTransferDto.getReceiverAccNumber()));

        transferValidate(moneyTransferDto.getAmount(), senderAccount, receiverAccount);

        Long senderAccBalance = senderAccount.getBalance();
        Long receiverAccBalance = receiverAccount.getBalance();
        Long sendingAmount = moneyTransferDto.getAmount();

        if (!senderAccount.getCurrency().getAlphaCode().equals(receiverAccount.getCurrency().getAlphaCode())) {
            CurrencyRate currencyRate = currencyRateRepository.findCurrencyRateByFromCurrAndToCurr(senderAccount.getCurrency().getAlphaCode(),
                    receiverAccount.getCurrency().getAlphaCode());

            sendingAmount = ((Double) (moneyTransferDto.getAmount() * currencyRate.getRate())).longValue();
        }

        senderAccount.setBalance(senderAccBalance - moneyTransferDto.getAmount());
        receiverAccount.setBalance(receiverAccBalance + sendingAmount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    private void transferValidate(Long sendingAmount, Account senderAccount, Account receiverAccount) {
        if (senderAccount.getBalance() < sendingAmount) {
            throw new NoEnoughMoneyException(senderAccount.getAccNumber());
        }
        if (senderAccount.equals(receiverAccount)) {
            throw new SameAccountException();
        }
        if (!receiverAccount.getIsActive()) {
            throw new DeactiveAccountException();
        }
    }
}
