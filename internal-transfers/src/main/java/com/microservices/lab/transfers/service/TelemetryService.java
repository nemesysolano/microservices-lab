package com.microservices.lab.transfers.service;

import com.microservices.lab.transfers.model.request.TransferRequest;

import java.util.Date;
import java.util.concurrent.CompletableFuture;


public interface TelemetryService {
    CompletableFuture<Date> saveTelemetryAsync(TransferRequest request);
}
