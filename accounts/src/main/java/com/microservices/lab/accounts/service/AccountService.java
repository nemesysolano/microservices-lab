package com.microservices.lab.accounts.service;

import com.microservices.lab.accounts.model.request.UpdateBalancesRequest;
import com.microservices.lab.accounts.model.response.QueryBalanceResponse;
import com.microservices.lab.accounts.model.response.UpdateBalancesResponse;

public interface AccountService {
    QueryBalanceResponse queryBalance(String accountId);
    UpdateBalancesResponse updateBalances(UpdateBalancesRequest request);
}
