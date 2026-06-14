package com.microservices.lab.transfers.service.impl;

import com.microservices.lab.transfers.model.request.UpdateBalancesRequest;
import com.microservices.lab.transfers.model.response.QueryBalanceResponse;
import com.microservices.lab.transfers.model.response.UpdateBalancesResponse;
import com.microservices.lab.transfers.service.AccountClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service("AccountClientImpl")
@Slf4j
public class AccountClientImpl implements AccountClient {
    String accountsServiceUrl;
    RestTemplate restTemplate;

    public AccountClientImpl(
        @Value("${spring.application.client.accounts.url}") String accountsServiceUrl
    ) {
        this.accountsServiceUrl = accountsServiceUrl;
        this.restTemplate = new RestTemplate();
    }
    public QueryBalanceResponse queryBalance(String accountId) {
        String uri = accountsServiceUrl + "/v1/accounts/"+accountId+"/balance";

        try {
            return restTemplate.getForObject(uri, QueryBalanceResponse.class);
        }catch(HttpClientErrorException.BadRequest cause) {
            log.error(String.format("BAD REQUEST response received from %s when fetching balance.", uri), cause);
            return QueryBalanceResponse.nonExisting();
        }
    }

    public UpdateBalancesResponse updateBalances(UpdateBalancesRequest request) {
        String uri = accountsServiceUrl + "/v1/accounts/";

        try {
            return restTemplate.postForObject(uri, request, UpdateBalancesResponse.class);
        }
        catch (HttpClientErrorException.BadRequest cause) {
            log.error(String.format("BAD REQUEST response received from %s updating balances", uri), cause);
            return new UpdateBalancesResponse(-100);
        }
    }
}
