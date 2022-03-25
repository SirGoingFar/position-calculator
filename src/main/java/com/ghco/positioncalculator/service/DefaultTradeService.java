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
import java.util.function.Consumer;
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
    public Map<String, Map<String, PortfolioPositionSummary>> aggregateTradePosition() {
        // 1. Get all unique BBGCodes
        final List<String> uniqueBbgCodes = tradeRepository.findAllUniqueBbgCodes();

        // 2. Get associated BBG Code's NEW trades & compose Map<BBGCode, List<Trade>>
        final Map<String, List<Trade>> bbgCodeToTradeMap = new HashMap<>();

        /*
         * Only NEW action is considered here because:
         * - trades with action AMEND has a corresponding trade with same tradeId and price
         * - trades with action CANCEL has been cancelled and shouldn't be considered in the P&L computation
         * */
        uniqueBbgCodes.forEach(bbgCode -> bbgCodeToTradeMap.put(bbgCode, tradeRepository.findAllByBbgCodeAndAction(bbgCode, Trade.Action.NEW)));

        //i.e. Map<BBGCode, Map<Portfolio, PositionSummary>>
        final Map<String, Map<String, PortfolioPositionSummary>> perBbgCodePerPortfolioAggregationMap = new HashMap<>();

        //Per BGG code, compute per portfolio
        bbgCodeToTradeMap.forEach((bbgCode, trades) -> {

            final Map<String, PortfolioPositionSummary> summaryMap = new HashMap<>();

            // 1. Group trades by portfolio
            //i.e. Map<Portfolio, List<Trade>>
            final Map<String, List<Trade>> portfolioToCorrespondingTrade = AggregationUtil.groupByPortfolio(trades);

            // 2. Categorize List<Trade> for each Portfolio to BUY/SELL
            portfolioToCorrespondingTrade.forEach((portfolio, portfolioTrades) -> {

                //Map<User, TotalSales>
                final Map<String, BigDecimal> userToTotalPortfolioSellMap = new HashMap<>();

                //Map<User, TotalPurchases>
                final Map<String, BigDecimal> userToTotalPortfolioBuyMap = new HashMap<>();

                final List<String> uniqueUsers = portfolioTrades.stream().map(Trade::getUser).distinct().collect(Collectors.toList());

                portfolioTrades.forEach(new Consumer<>() {
                    @Override
                    public void accept(Trade trade) {
                        if (trade.getSide() == Trade.Side.SELL) {
                            addOrIncrementValue(userToTotalPortfolioSellMap, trade.getUser(), trade.getPrice());
                        } else {
                            addOrIncrementValue(userToTotalPortfolioBuyMap, trade.getUser(), trade.getPrice());
                        }
                    }

                    private void addOrIncrementValue(@NonNull final Map<String, BigDecimal> totalPriceAggregateMap, @NonNull final String user, @NonNull final BigDecimal price) {
                        if (totalPriceAggregateMap.containsKey(user)) {
                            totalPriceAggregateMap.put(user, totalPriceAggregateMap.get(user).add(price));
                        } else {
                            totalPriceAggregateMap.put(user, price);
                        }
                    }
                });

                // 3. Per User, compute P/L
                BigDecimal totalProfit = BigDecimal.ZERO;
                BigDecimal totalLoss = BigDecimal.ZERO;

                for (String user : uniqueUsers) {
                    BigDecimal userTotalSellPrice = userToTotalPortfolioSellMap.getOrDefault(user, BigDecimal.ZERO);
                    BigDecimal userTotalBuyPrice = userToTotalPortfolioBuyMap.getOrDefault(user, BigDecimal.ZERO);
                    BigDecimal diff = userTotalBuyPrice.subtract(userTotalSellPrice);

                    if(diff.compareTo(BigDecimal.ZERO) < 0){
                        totalLoss = totalLoss.add(diff.abs());
                    }else{
                        totalProfit = totalProfit.add(diff.abs());
                    }
                }

                PortfolioPositionSummary summary = PortfolioPositionSummary.builder()
                        .profit(totalProfit)
                        .loss(totalLoss)
                        .build();

                summaryMap.put(portfolio, summary);
            });

            perBbgCodePerPortfolioAggregationMap.put(bbgCode, summaryMap);

        });

        return perBbgCodePerPortfolioAggregationMap;
    }

}
