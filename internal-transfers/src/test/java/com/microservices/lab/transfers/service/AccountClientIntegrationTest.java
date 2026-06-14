package com.microservices.lab.transfers.service;

import com.microservices.lab.transfers.model.request.UpdateBalancesRequest;
import com.microservices.lab.transfers.model.response.QueryBalanceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AccountClientIntegrationTest {
    @Autowired
    AccountClient accountClient;


    @Test
    public void queryBalanceTest() {
        QueryBalanceResponse response = accountClient.queryBalance("John.Doe");
        assertTrue(response.balance.compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(response.timestamp);
    }

    @Test
    public void updateBalanceTest() {
        UpdateBalancesRequest updateBalancesRequest = new UpdateBalancesRequest("John.Doe", "Marty.McFly", BigDecimal.TEN);
        accountClient.updateBalances(updateBalancesRequest);
        QueryBalanceResponse johnDoeBalance = accountClient.queryBalance("John.Doe");
        QueryBalanceResponse martyMcFlyBalance = accountClient.queryBalance("Marty.McFly");
        assertTrue(johnDoeBalance.balance.compareTo(new BigDecimal("90")) == 0);
        assertTrue(martyMcFlyBalance.balance.compareTo(new BigDecimal("110")) == 0);

        UpdateBalancesRequest reverseBalancesRequest = new UpdateBalancesRequest( "Marty.McFly", "John.Doe", BigDecimal.TEN);
        accountClient.updateBalances(reverseBalancesRequest);
        johnDoeBalance = accountClient.queryBalance("John.Doe");
        martyMcFlyBalance = accountClient.queryBalance("Marty.McFly");
        assertTrue(johnDoeBalance.balance.compareTo(new BigDecimal("100")) == 0);
        assertTrue(martyMcFlyBalance.balance.compareTo(new BigDecimal("100")) == 0);
    }
}
