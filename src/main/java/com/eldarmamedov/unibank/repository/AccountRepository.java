package com.eldarmamedov.unibank.repository;

import com.eldarmamedov.unibank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccNumber(String accNumber);

    List<Account> findAccountsByUserId(Long id);

}
