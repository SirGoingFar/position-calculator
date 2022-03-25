package com.ghco.positioncalculator.dto;

import com.ghco.positioncalculator.model.Trade;
import lombok.Builder;
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

    @Builder
    public AddTradeRequestDto(String bbgCode, String currency, Trade.Side side, BigDecimal price, Double volume,
                              String portfolio, Trade.Action action, String account, String strategy, String user,
                              Instant tradeTime, String valueDate) {
        this.bbgCode = bbgCode;
        this.currency = currency;
        this.side = side;
        this.price = price;
        this.volume = volume;
        this.portfolio = portfolio;
        this.action = action;
        this.account = account;
        this.strategy = strategy;
        this.user = user;
        this.tradeTime = tradeTime;
        this.valueDate = valueDate;
    }
}
