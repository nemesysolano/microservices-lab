package com.microservices.lab.transfers.model.response;

import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class UpdateBalancesResponse {
    public final String sourceAccountId;
    public final BigDecimal sourceAccountBalance;
    public final String destinationAccountId;
    public final BigDecimal destinationAccountBalance;
    public final int errorCode; // 0 Success, any other value indicates a failure.

    public UpdateBalancesResponse(int errorCode) {
        this(null, null, null, null, errorCode);
    }

    public boolean successful() {
        return errorCode == 0;
    }
}
