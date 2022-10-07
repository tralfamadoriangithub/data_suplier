package com.example.data_suplier.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Balance {

    private String rawBalance;
    private BigDecimal amount;
    private String currency;

}
