package com.microservices.lab.accounts.controller;

import com.microservices.lab.accounts.model.request.UpdateBalancesRequest;
import com.microservices.lab.accounts.model.response.QueryBalanceResponse;
import com.microservices.lab.accounts.model.response.UpdateBalancesResponse;
import com.microservices.lab.accounts.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("AccountController")
@RequestMapping("/v1/accounts")
public class AccountController {
    final private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<QueryBalanceResponse> queryBalance(@PathVariable("accountId") String accountId) {
        QueryBalanceResponse queryBalanceResponse = accountService.queryBalance(accountId);
        if (queryBalanceResponse.isNonExisting()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(queryBalanceResponse);
    }

    @PostMapping("/")
    public ResponseEntity<UpdateBalancesResponse> updateBalances(@RequestBody UpdateBalancesRequest request) {
        UpdateBalancesResponse updateBalancesResponse = accountService.updateBalances(request);
        if(updateBalancesResponse.successful()) {
            return ResponseEntity.ok(updateBalancesResponse);
        }
        return ResponseEntity.badRequest().body(updateBalancesResponse);
    }
}
