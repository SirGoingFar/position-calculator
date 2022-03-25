package com.ghco.positioncalculator.service;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.dto.PortfolioPositionSummary;
import lombok.NonNull;

import java.util.Map;

public interface TradeService {
    Map<String, Map<String, PortfolioPositionSummary>> aggregateTradePosition();

    AddTradeResponseDto addTrade(@NonNull final AddTradeRequestDto requestDto);
}
