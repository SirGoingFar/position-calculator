package com.ghco.positioncalculator.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class Trade {

    private String id;
    private String bbgCode;
    private String currency;
    private Side side;
    private BigDecimal price;
    private Double volume;
    private String portfolio;
    private Action action;
    private String account;
    private String strategy;
    private String user;
    private Instant tradeTime;
    private String valueDate;

    @Builder
    private Trade(String id, String bbgCode, String currency, Side side, BigDecimal price, Double volume,
                  String portfolio, Action action, String account, String strategy, String user, Instant tradeTime,
                  String valueDate) {
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

    public enum Side {
        SELL, //for S
        BUY; //for B

        @Nullable
        public static Side of(@Nullable String side) {
            if(!StringUtils.hasText(side)){
                return null;
            }

            for (Side s : values()) {
                if (side.equals(String.valueOf(s.name().charAt(0)))) {
                    return s;
                }
            }

            return null;
        }
    }

    public enum Action {
        NEW,
        AMEND,
        CANCEL;

        @Nullable
        public static Action of(@Nullable String action) {
            if(!StringUtils.hasText(action)){
                return null;
            }

            for (Action a : values()) {
                if (action.equals(a.name())) {
                    return a;
                }
            }

            return null;
        }
    }
}
