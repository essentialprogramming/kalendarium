package com.identityprovider.repository;

import com.identityprovider.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserKey(String userKey);
}
