package com.microservices.lab.accounts.controller;

import com.microservices.lab.accounts.model.request.UpdateBalancesRequest;
import com.microservices.lab.accounts.model.response.QueryBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class AccountControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("UPDATE ACCOUNT SET ACCOUNT_BALANCE = 100.00");
    }

    @Test
    @DisplayName("Query balance success")
    public void queryBalanceSuccessTest() throws Exception {

        var response = queryBalance("John.Doe");
        assertNotNull(response.timestamp);
    }

    @Test
    @DisplayName("Covers all invalid request scenarios")
    public void updateBalanceFailuresTest() throws Exception {
        UpdateBalancesRequest request = UpdateBalancesRequest.builder().sourceAccountId("Any.One").build();
        expectsFailure(request, -1, status().isBadRequest());

        request = request.toBuilder().sourceAccountId("John.Doe").destinationAccountId("Any.One").build();
        expectsFailure(request, -2, status().isBadRequest());

        request = request.toBuilder().destinationAccountId("Marty.McFly").amount(BigDecimal.ONE.subtract(BigDecimal.TWO)).build();
        expectsFailure(request, -3, status().isBadRequest());

        request = request.toBuilder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN)).build();
        expectsFailure(request, -4, status().isBadRequest());
    }

    @Test
    @DisplayName("Update balance success")
    public void updateBalanceSuccessTest() throws Exception {
        UpdateBalancesRequest updateBalancesRequest = new UpdateBalancesRequest("John.Doe", "Marty.McFly", BigDecimal.TEN);;
        expectsFailure(updateBalancesRequest, 0, status().isOk());

        QueryBalanceResponse johnBalance = queryBalance("John.Doe");
        QueryBalanceResponse martyBalance = queryBalance("Marty.McFly");
        BigDecimal difference = martyBalance.balance.subtract(johnBalance.balance);
        assertEquals(difference, new BigDecimal(20));
    }

    void expectsFailure(UpdateBalancesRequest request, int errorCode, ResultMatcher resultMatcher) throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/accounts/")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(resultMatcher)
        .andExpect(jsonPath("$.errorCode").value(Integer.toString(errorCode)))
        .andReturn();
    }

    public QueryBalanceResponse queryBalance(String accountId) throws Exception {
        final var result = mockMvc.perform(
            MockMvcRequestBuilders.get(String.format("/v1/accounts/%s/balance", accountId))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), QueryBalanceResponse.class);
    }
}
