package com.microservices.lab.transfers.controller;

import com.microservices.lab.transfers.model.request.TransferRequest;
import com.microservices.lab.transfers.model.response.TransferResponse;
import com.microservices.lab.transfers.service.AccountClient;
import com.microservices.lab.transfers.service.TelemetryService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountClient accountClient;

    @MockitoSpyBean
    TelemetryService transferService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void transferTest() throws Exception {
        TransferRequest transferRequest = new TransferRequest("John.Doe", "Marty.McFly", BigDecimal.TEN, RandomString.make(32));
        transfer(transferRequest, 0, MockMvcResultMatchers.status().isOk());

        var johnBalance = accountClient.queryBalance("John.Doe");
        var martybalance= accountClient.queryBalance("Marty.McFly");
        assertTrue(johnBalance.balance.compareTo(martybalance.balance) < 0);

        TransferRequest reverseRequest = new TransferRequest( "Marty.McFly", "John.Doe", BigDecimal.TEN, RandomString.make(32));
        transfer(reverseRequest, 0, MockMvcResultMatchers.status().isOk());

        johnBalance = accountClient.queryBalance("John.Doe");
        martybalance= accountClient.queryBalance("Marty.McFly");
        assertTrue(johnBalance.balance.compareTo(martybalance.balance) == 0);

        verify(transferService, times(2)).saveTelemetryAsync(any());
    }

    @Test
    void transferWithoutTelemetryTest() throws Exception {
        TransferRequest transferRequest = new TransferRequest("John.Doe", "Marty.McFly", BigDecimal.TEN, null);
        transfer(transferRequest, -101, MockMvcResultMatchers.status().isBadRequest());
        transfer(transferRequest.toBuilder().telemetry("").build(), -101, MockMvcResultMatchers.status().isBadRequest());
        transfer(transferRequest.toBuilder().telemetry("  ").build(), -101, MockMvcResultMatchers.status().isBadRequest());
    }

    TransferResponse transfer(TransferRequest request, int errorCode, ResultMatcher resultMatcher) throws Exception {
        var result = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/transfers/")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(resultMatcher)
        .andExpect(jsonPath("$.errorCode").value(Integer.toString(errorCode)))
        .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), TransferResponse.class);
    }
}
