package com.eldarmamedov.unibank.controller;

import com.eldarmamedov.unibank.dto.AccountDto;
import com.eldarmamedov.unibank.dto.MoneyTransferDto;
import com.eldarmamedov.unibank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final AccountService accountService;

    @GetMapping("/{id}/accounts")
    public List<AccountDto> myActiveAccounts(@PathVariable Long id) {
        return accountService.findActiveAccountsByUser(id);
    }

    @PostMapping("/send")
    public void sendMoney(@RequestBody MoneyTransferDto moneyTransferDto) {
        accountService.sendMoney(moneyTransferDto);
    }
}


