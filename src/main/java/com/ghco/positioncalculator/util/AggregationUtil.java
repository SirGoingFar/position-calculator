package com.ghco.positioncalculator.util;

import com.ghco.positioncalculator.model.Trade;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class AggregationUtil {
    private AggregationUtil() {
        throw new IllegalStateException("Cannot instantiate AggregationUtil");
    }

    public static Map<String, List<Trade>> groupByBbgCode(@NonNull final List<Trade> trades) {
        return trades.stream().collect(groupingBy(Trade::getBbgCode));
    }

    public static List<Trade> filterBySide(@NonNull final List<Trade> trades, @NonNull final Trade.Side side) {
        return trades.stream().filter(trade -> trade.getSide() == side).distinct().collect(Collectors.toList());
    }

}
