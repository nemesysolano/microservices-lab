package com.microservices.lab.transfers.service;

import com.microservices.lab.transfers.model.request.UpdateBalancesRequest;
import com.microservices.lab.transfers.model.response.QueryBalanceResponse;
import com.microservices.lab.transfers.model.response.UpdateBalancesResponse;

public interface AccountClient {
    QueryBalanceResponse queryBalance(String accountId);
    UpdateBalancesResponse updateBalances(UpdateBalancesRequest request);
}
