package com.microservices.lab.transfers.model.request;

import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class TransferRequest {
    public final String sourceAccountId;
    public final String destinationAccountId;
    public final BigDecimal amount;
    public final String telemetry;
}
