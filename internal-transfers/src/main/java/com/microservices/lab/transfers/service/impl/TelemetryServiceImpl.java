package com.microservices.lab.transfers.service.impl;

import com.microservices.lab.transfers.model.entity.Telemetry;
import com.microservices.lab.transfers.model.request.TransferRequest;
import com.microservices.lab.transfers.repository.TelemetryRepository;
import com.microservices.lab.transfers.service.TelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TelemetryServiceImpl implements TelemetryService {
    TelemetryRepository telemetryRepository;

    public TelemetryServiceImpl(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }


    @Override
    @Async("telemetryExecutor")
    public CompletableFuture<Date> saveTelemetryAsync(TransferRequest request) {
        try {
            telemetryRepository.save(Telemetry.builder().content(request.telemetry).build());
            logInfo(String.format("%s telemetry was saved to database", request.telemetry));
        } catch(DataAccessException cause) {
            logError(String.format("Can't save %s telemetry to database", request.telemetry), cause);
        } catch(Exception cause) {
            logError(String.format("Can't save %s telemetry to database (Uncaught exception)", request.telemetry), cause);
        }

        return CompletableFuture.completedFuture(new Date());
    }

    void logInfo(String info) {
        log.info(info);
    }

    void logError(String error, Throwable cause) {
        log.error(error, cause);
    }
}
