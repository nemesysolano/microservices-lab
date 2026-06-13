package com.microservices.lab.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import com.microservices.lab.accounts.model.entity.Account;

public interface AccountRepository extends CrudRepository<Account, String> {
}
