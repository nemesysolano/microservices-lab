package com.microservices.lab.accounts.service.impl;

import com.microservices.lab.accounts.model.entity.Account;
import com.microservices.lab.accounts.model.request.UpdateBalancesRequest;
import com.microservices.lab.accounts.model.response.QueryBalanceResponse;
import com.microservices.lab.accounts.model.response.UpdateBalancesResponse;
import com.microservices.lab.accounts.repository.AccountRepository;
import com.microservices.lab.accounts.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service("AccountService")
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {
    AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public QueryBalanceResponse queryBalance(String accountId) {
        Optional<Account> findAccountResult = accountRepository.findById(accountId);
        return findAccountResult.map(account -> new QueryBalanceResponse(account.accountBalance, new Date())).orElseGet(QueryBalanceResponse::nonExisting);
    }

    @Override
    public UpdateBalancesResponse updateBalances(UpdateBalancesRequest request) {
        Optional<Account> findSourceAccountResult = accountRepository.findById(request.sourceAccountId);
        if (findSourceAccountResult.isEmpty()) {
            log.error("Source account with ID {} not found", request.sourceAccountId);
            return new UpdateBalancesResponse(-1);
        }

        Optional<Account> findDestinationAccountResult = accountRepository.findById(request.destinationAccountId);

        if (findDestinationAccountResult.isEmpty()) {
            log.error("Destination account with ID {} not found", request.destinationAccountId);
            return new UpdateBalancesResponse(-2);
        }

        Account sourceAccount = findSourceAccountResult.get();
        Account destinationAccount = findDestinationAccountResult.get();

        if (request.amount == null || request.amount.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid amount: {}", request.amount);
            return new UpdateBalancesResponse(-3);
        }

        if (request.amount.compareTo(sourceAccount.accountBalance) > 0){
            log.error("Insufficient funds in source account {}. Requested amount: {}, Available balance: {}", request.sourceAccountId, request.amount, sourceAccount.accountBalance);
            return new UpdateBalancesResponse(-4);
        }

        sourceAccount = sourceAccount.toBuilder().accountBalance(sourceAccount.accountBalance.subtract(request.amount)).build();
        destinationAccount = destinationAccount.toBuilder().accountBalance(destinationAccount.accountBalance.add(request.amount)).build();
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        UpdateBalancesResponse response = UpdateBalancesResponse
            .builder()
            .sourceAccountId(request.sourceAccountId)
            .destinationAccountId(request.destinationAccountId)
            .sourceAccountBalance(sourceAccount.accountBalance)
            .destinationAccountBalance(destinationAccount.accountBalance)
            .build();
        log.info("Balances updated successfully for source account {} ({}) and destination account {} ({})", request.sourceAccountId, response.sourceAccountBalance,  request.destinationAccountId, response.destinationAccountBalance);
        return response;
    }
}
