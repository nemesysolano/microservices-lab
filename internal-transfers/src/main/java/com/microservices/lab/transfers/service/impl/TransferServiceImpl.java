package com.microservices.lab.transfers.service.impl;

import com.microservices.lab.transfers.model.request.TransferRequest;
import com.microservices.lab.transfers.model.request.UpdateBalancesRequest;
import com.microservices.lab.transfers.model.response.TransferResponse;
import com.microservices.lab.transfers.model.response.UpdateBalancesResponse;
import com.microservices.lab.transfers.service.AccountClient;
import com.microservices.lab.transfers.service.NotificationService;
import com.microservices.lab.transfers.service.TelemetryService;
import com.microservices.lab.transfers.service.TransferService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("TransferService")
@Slf4j
public class TransferServiceImpl implements TransferService{
    AccountClient accountClient;
    TelemetryService telemetryService;
    NotificationService notificationService;

    public TransferServiceImpl(AccountClient accountClient, TelemetryService telemetryService, NotificationService notificationService) {
        this.accountClient = accountClient;
        this.telemetryService = telemetryService;
        this.notificationService = notificationService;
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        // check if request.telemetry is either blank, empty or null
        if(StringUtils.isBlank(request.telemetry)) {
            return TransferResponse.builder().errorCode(-101).build();
        } else {
            telemetryService.saveTelemetryAsync(request);
        }

        UpdateBalancesRequest updateBalancesRequest = UpdateBalancesRequest.builder()
            .amount(request.amount)
            .destinationAccountId(request.destinationAccountId)
            .sourceAccountId(request.sourceAccountId)
            .build();

        UpdateBalancesResponse response = accountClient.updateBalances(updateBalancesRequest);
        if(response.errorCode == 0) {
            notificationService.notifyAsync(String.format("%s sent %s to %s on %s", request.sourceAccountId, request.amount, request.destinationAccountId, new Date()));
            return TransferResponse.builder().errorCode(0).build();
        } else {
            logError(String.format("Received error code %s from accountClient when posting %s", response.errorCode, updateBalancesRequest));
            return TransferResponse.builder().errorCode(response.errorCode).build();
        }
    }

    void logError(String error) {
        log.error(error);
    }



}
