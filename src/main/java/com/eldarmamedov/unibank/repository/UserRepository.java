package com.eldarmamedov.unibank.repository;

import com.eldarmamedov.unibank.entity.Account;
import com.eldarmamedov.unibank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

}
