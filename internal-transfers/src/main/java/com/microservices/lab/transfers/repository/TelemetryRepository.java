package com.microservices.lab.transfers.repository;

import com.microservices.lab.transfers.model.entity.Telemetry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TelemetryRepository extends MongoRepository<Telemetry, String> {
}
