package com.ghco.positioncalculator.service;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.dto.BbgCodePositionSummary;
import lombok.NonNull;

import java.util.Map;

public interface TradeService {
    Map<String, Map<String, BbgCodePositionSummary>> aggregateTradePosition();

    AddTradeResponseDto addTrade(@NonNull final AddTradeRequestDto requestDto);
}
