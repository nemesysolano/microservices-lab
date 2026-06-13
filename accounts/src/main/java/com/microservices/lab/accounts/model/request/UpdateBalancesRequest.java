package com.microservices.lab.accounts.model.request;

import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class UpdateBalancesRequest {
    public final String sourceAccountId;
    public final String destinationAccountId;
    public final BigDecimal amount;
}
