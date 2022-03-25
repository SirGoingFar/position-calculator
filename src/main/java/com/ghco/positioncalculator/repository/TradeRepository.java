package com.ghco.positioncalculator.repository;

import com.ghco.positioncalculator.model.Trade;
import lombok.NonNull;

import java.util.List;

public interface TradeRepository {
    List<Trade> findAll();

    List<Trade> findAllBySide(@NonNull final Trade.Side side);

    List<Trade> findAllByBbgCode(@NonNull final String bbgCode);

    Trade save(@NonNull final Trade trade);

    List<String> findAllUniquePortfolio();

    List<Trade> findAllByPortfolioAndAction(@NonNull final String portfolio, @NonNull final Trade.Action action);
}
