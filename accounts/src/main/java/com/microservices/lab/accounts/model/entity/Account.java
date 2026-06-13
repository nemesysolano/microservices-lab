package com.microservices.lab.accounts.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ACCOUNT")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public class Account {
    @Id
    @Column(name = "ACCOUNT_ID")
    public final String accountId;
    @Column(name = "ACCOUNT_NAME")
    public final String accountName;
    @Column(name = "ACCOUNT_BALANCE")
    public final BigDecimal accountBalance;
    @Column(name = "ACCOUNT_EMAIL")
    public final String accountEmail;
    @Version
    @Column(name = "VERSION")
    public final long version;
}
