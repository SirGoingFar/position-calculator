package com.ghco.positioncalculator.dto;

import com.ghco.positioncalculator.model.Trade;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AddTradeResponseDto {
    private String id;
    private String bbgCode;
    private String currency;
    private Trade.Side side;
    private BigDecimal price;
    private Double volume;
    private String portfolio;
    private Trade.Action action;
    private String account;
    private String strategy;
    private String user;
    private Instant tradeTime;
    private String valueDate;

    @Builder
    public AddTradeResponseDto(String id, String bbgCode, String currency, Trade.Side side, BigDecimal price,
                               Double volume, String portfolio, Trade.Action action, String account, String strategy,
                               String user, Instant tradeTime, String valueDate) {
        this.id = id;
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
