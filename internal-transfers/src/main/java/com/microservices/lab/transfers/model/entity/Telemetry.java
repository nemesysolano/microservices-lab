package com.microservices.lab.transfers.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
@Document(collection = "internal-transfer")
public class Telemetry {
    @Id
    public final String id;
    public final String content;

    @PersistenceCreator
    public Telemetry(String id, String content) {
        this.id = id;
        this.content = content;
    }
}
