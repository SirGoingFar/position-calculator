package com.ghco.positioncalculator.repository;

import com.ghco.positioncalculator.model.Trade;
import lombok.NonNull;

import java.util.List;

public interface TradeRepository {
    List<Trade> findAll();

    Trade save(@NonNull final Trade trade);

    List<String> findAllUniqueBbgCodes();

    List<Trade> findAllByBbgCodeAndAction(@NonNull final String bbgCode, @NonNull final Trade.Action action);
}
