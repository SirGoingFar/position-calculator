package com.ghco.positioncalculator.service;

import com.ghco.positioncalculator.dto.*;
import com.ghco.positioncalculator.model.Trade;
import com.ghco.positioncalculator.repository.TradeRepository;
import com.ghco.positioncalculator.util.AggregationUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<String, Map<String,BbgCodePositionSummary>> aggregateTradePosition() {
        // 1. Get all unique portfolio
        final List<String> uniquePortfolios = tradeRepository.findAllUniquePortfolio();

        // 2. Get associated portfolio NEW trades & compose Map<Portfolio, List<Trade>>
        final Map<String, List<Trade>> portfolioToTradeMap = new HashMap<>();
        uniquePortfolios.forEach(portfolio -> portfolioToTradeMap.put(portfolio, tradeRepository.findAllByPortfolioAndAction(portfolio, Trade.Action.NEW)));

        //i.e. Map<Portfolio, Map<BBGCode, PositionSummary>>
        final Map<String, Map<String,BbgCodePositionSummary>> perPortfolioBbgCodeAggregationMap = new HashMap<>();

        //Per portfolio, compute per BBG code
        portfolioToTradeMap.forEach((portfolio, trades) -> {

            // 1. Filter out unique BBG code
            //i.e. Map<BBGCode, List<Trade>>
            final Map<String, List<Trade>> bbgCodeToCorrespondingTrade = AggregationUtil.groupByBbgCode(trades);

            final Map<String, BbgCodePositionSummary> summaryMap = new HashMap<>();
            // 2. Split List<Trade> for each BBG code to BUY & SELL
            bbgCodeToCorrespondingTrade.forEach((bbgCode, bbgCodeTrades) -> {

                //Map<User, TotalSales>
                final Map<String, BigDecimal> userToTotalBbgSellMap = new HashMap<>();

                //Map<User, TotalPurchases>
                final Map<String, BigDecimal> userToTotalBbgBuyMap = new HashMap<>();

                final List<String> uniqueUsers = bbgCodeTrades.stream().map(Trade::getUser).distinct().collect(Collectors.toList());

                bbgCodeTrades.forEach(new Consumer<>() {
                    @Override
                    public void accept(Trade trade) {
                        if (trade.getSide() == Trade.Side.SELL) {
                            addOrIncrementValue(userToTotalBbgSellMap, trade.getUser(), trade.getPrice());
                        } else {
                            addOrIncrementValue(userToTotalBbgBuyMap, trade.getUser(), trade.getPrice());
                        }
                    }

                    private void addOrIncrementValue(@NonNull final Map<String, BigDecimal> aggregateMap, @NonNull final String user, @NonNull final BigDecimal price) {
                        if (aggregateMap.containsKey(user)) {
                            BigDecimal existingValue = aggregateMap.get(user);
                            aggregateMap.put(user, existingValue.add(price));
                        } else {
                            aggregateMap.put(user, price);
                        }
                    }
                });

                // 3. Per User, compute P/L
                BigDecimal totalProfit = BigDecimal.ZERO;
                BigDecimal totalLoss = BigDecimal.ZERO;

                for (String user : uniqueUsers) {
                    BigDecimal userTotalSellPrice = userToTotalBbgSellMap.getOrDefault(user, BigDecimal.ZERO);
                    BigDecimal userTotalBuyPrice = userToTotalBbgBuyMap.getOrDefault(user, BigDecimal.ZERO);
                    BigDecimal bal = userTotalBuyPrice.subtract(userTotalSellPrice);

                    if(bal.compareTo(BigDecimal.ZERO) < 0){
                        totalLoss = totalLoss.add(bal.abs());
                    }else{
                        totalProfit = totalProfit.add(bal.abs());
                    }
                }

                BbgCodePositionSummary summary = BbgCodePositionSummary.builder()
                        .profit(totalProfit)
                        .loss(totalLoss)
                        .build();


                summaryMap.put(bbgCode, summary);
            });

            perPortfolioBbgCodeAggregationMap.put(portfolio, summaryMap);

        });

        return perPortfolioBbgCodeAggregationMap;
    }

}
