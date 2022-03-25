package com.ghco.positioncalculator.service;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.dto.PositionAggregation;
import lombok.NonNull;

public interface TradeService {
    PositionAggregation aggregateTradePosition();

    AddTradeResponseDto addTrade(@NonNull final AddTradeRequestDto requestDto);
}
