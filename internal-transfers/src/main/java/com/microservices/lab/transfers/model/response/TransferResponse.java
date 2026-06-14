package com.microservices.lab.transfers.model.response;

import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class TransferResponse {
    public final int errorCode; // 0 Success, any other value indicates a failure.
    public boolean successful() {
        return errorCode == 0;
    }
}
