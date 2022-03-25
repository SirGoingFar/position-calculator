package com.ghco.positioncalculator.repository;

import com.ghco.positioncalculator.model.Trade;
import com.ghco.positioncalculator.repository.CsvBackedTradeRepository;
import com.ghco.positioncalculator.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnit4.class)
class CsvBackedTradeRepositoryTest {

    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository = new CsvBackedTradeRepository(getMockTradeData());
    }


    @Test
    void findAll_returnsAllTrades() {
        assertEquals(getMockTradeData().size(), tradeRepository.findAll().size());
    }

    @Test
    void saveTrade_tradeSavedSuccessfullyAtTopOfTradeData() {
        Trade trade = getOne();
        Trade savedTrade = tradeRepository.save(trade);
        assertEquals(trade.getSide(), savedTrade.getSide());
        assertEquals(trade.getPrice(), savedTrade.getPrice());
        assertEquals(trade.getPortfolio(), savedTrade.getPortfolio());
        assertEquals(trade.getAccount(), savedTrade.getAccount());
        assertEquals(trade.getTradeTime(), savedTrade.getTradeTime());
        assertEquals(trade.getStrategy(), savedTrade.getStrategy());

        Trade firstTradeOnTradeData = tradeRepository.findAll().get(0);
        assertEquals(savedTrade, firstTradeOnTradeData);
    }

    @Test
    void findAllUniqueBbgCode_returnsUniqueBbgCodes(){
        List<String> uniqueBbgCodes = List.of("GHCO UK", "GHCO US");
        List<String> returnedUniqueBbgCode = tradeRepository.findAllUniqueBbgCodes();
        uniqueBbgCodes.forEach(bbgCode -> assertTrue(returnedUniqueBbgCode.contains(bbgCode)));
    }

    @Test
    void findAllByBbgCodeAndAction_returnsTrade(){
        assertEquals(0, tradeRepository.findAllByBbgCodeAndAction("GHCO UK", Trade.Action.CANCEL).size());
    }

    private List<Trade> getMockTradeData() {
        return new ArrayList<>(
                List.of(
                        Trade.builder()
                                .id(UUID.randomUUID().toString().replace("-", ""))
                                .bbgCode("GHCO UK")
                                .currency("GBP")
                                .side(Trade.Side.BUY)
                                .price(BigDecimal.valueOf(2345))
                                .volume(1000.0)
                                .portfolio("portfolio1")
                                .action(Trade.Action.NEW)
                                .account("account2")
                                .strategy("strategy4")
                                .user("user2")
                                .tradeTime(Instant.now())
                                .valueDate("20220325")
                                .build(),
                        Trade.builder()
                                .id(UUID.randomUUID().toString().replace("-", ""))
                                .bbgCode("GHCO US")
                                .currency("USD")
                                .side(Trade.Side.SELL)
                                .price(BigDecimal.valueOf(600))
                                .volume(100.0)
                                .portfolio("portfolio1")
                                .action(Trade.Action.NEW)
                                .account("account1")
                                .strategy("strategy3")
                                .user("user1")
                                .tradeTime(Instant.now())
                                .valueDate("20220325")
                                .build(),
                        Trade.builder()
                                .id(UUID.randomUUID().toString().replace("-", ""))
                                .bbgCode("GHCO UK")
                                .currency("GBP")
                                .side(Trade.Side.BUY)
                                .price(BigDecimal.valueOf(234))
                                .volume(90.0)
                                .portfolio("portfolio1")
                                .action(Trade.Action.NEW)
                                .account("account2")
                                .strategy("strategy3")
                                .user("user2")
                                .tradeTime(Instant.now())
                                .valueDate("20220325")
                                .build(),
                        Trade.builder()
                                .id(UUID.randomUUID().toString().replace("-", ""))
                                .bbgCode("GHCO UK")
                                .currency("GBP")
                                .side(Trade.Side.BUY)
                                .price(BigDecimal.valueOf(23))
                                .volume(60.0)
                                .portfolio("portfolio2")
                                .action(Trade.Action.AMEND)
                                .account("account1")
                                .strategy("strategy6")
                                .user("user2")
                                .tradeTime(Instant.now())
                                .valueDate("20220325")
                                .build()
                )
        );
    }

    private Trade getOne() {
        return Trade.builder()
                .bbgCode("GHCO UK")
                .currency("GBP")
                .side(Trade.Side.SELL)
                .price(BigDecimal.valueOf(23))
                .volume(60.0)
                .portfolio("portfolio2")
                .action(Trade.Action.CANCEL)
                .account("account1")
                .strategy("strategy6")
                .user("user2")
                .tradeTime(Instant.now())
                .valueDate("20220325")
                .build();
    }

}