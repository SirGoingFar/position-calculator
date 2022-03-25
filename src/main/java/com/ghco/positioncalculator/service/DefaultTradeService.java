package com.ghco.positioncalculator.service;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.dto.PositionAggregation;
import com.ghco.positioncalculator.model.Trade;
import com.ghco.positioncalculator.repository.TradeRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
class DefaultTradeService implements TradeService {

    private final TradeRepository tradeRepository;

    public DefaultTradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public AddTradeResponseDto addTrade(@NonNull AddTradeRequestDto requestDto) {
        Trade trade = tradeRepository.save(Trade.builder()
                .bbgCode(requestDto.getBbgCode())
                .currency(requestDto.getCurrency())
                .side(requestDto.getSide())
                .price(requestDto.getPrice())
                .volume(requestDto.getVolume())
                .portfolio(requestDto.getPortfolio())
                .action(requestDto.getAction())
                .account(requestDto.getAccount())
                .strategy(requestDto.getStrategy())
                .user(requestDto.getUser())
                .tradeTime(requestDto.getTradeTime())
                .valueDate(requestDto.getValueDate())
                .build());

        return AddTradeResponseDto.builder()
                .id(trade.getId())
                .bbgCode(trade.getBbgCode())
                .currency(trade.getCurrency())
                .side(trade.getSide())
                .price(trade.getPrice())
                .volume(trade.getVolume())
                .portfolio(trade.getPortfolio())
                .action(trade.getAction())
                .account(trade.getAccount())
                .strategy(trade.getStrategy())
                .user(trade.getUser())
                .tradeTime(trade.getTradeTime())
                .valueDate(trade.getValueDate())
                .build();
    }

    @Override
    public PositionAggregation aggregateTradePosition() {
        // 1. Get all unique portfolio
        // 2. Get associated portfolio NEW trades
        // 3. Compose Map<Portfolio, List<Trade>>

        //Per portfolio, compute per BBG code
        // 1. Filter out unique BBG code
        // 2. Split List<Trade> for each BBG code to BUY & SELL
        // 3. Per User, compute
        return null;
    }

}
