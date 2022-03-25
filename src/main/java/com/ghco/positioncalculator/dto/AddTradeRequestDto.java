package com.ghco.positioncalculator.dto;

import com.ghco.positioncalculator.model.Trade;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AddTradeRequestDto {

    @NotBlank(message = "is required")
    private String bbgCode;

    @NotBlank(message = "is required")
    private String currency;

    @NotBlank(message = "is required")
    private Trade.Side side;

    @NotNull(message = "is required")
    private BigDecimal price;

    @NotNull(message = "is required")
    private Double volume;

    @NotBlank(message = "is required")
    private String portfolio;

    @NotNull(message = "is required")
    private Trade.Action action;

    @NotBlank(message = "is required")
    private String account;

    @NotBlank(message = "is required")
    private String strategy;

    @NotBlank(message = "is required")
    private String user;

    @NotNull(message = "is required")
    private Instant tradeTime;

    @NotBlank(message = "is required")
    private String valueDate;
}
