package com.microservices.lab.transfers.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class QueryBalanceResponse {
    public final BigDecimal balance;
    public final Date timestamp;

    public boolean isNonExisting() {
        return (balance == null || balance.compareTo(BigDecimal.ZERO) == 0) && timestamp == null;
    }

    public static QueryBalanceResponse nonExisting() {
        return new QueryBalanceResponse(BigDecimal.ZERO, null);
    }
}
