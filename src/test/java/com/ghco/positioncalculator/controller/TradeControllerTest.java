package com.ghco.positioncalculator.controller;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.model.Trade;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TradeControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    void createTrade() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/trade/add";
        URI uri = new URI(baseUrl);
        AddTradeRequestDto requestDto = AddTradeRequestDto.builder()
                .bbgCode("GHCO UK")
                .currency("GBP")
                .side(Trade.Side.SELL)
                .price(BigDecimal.valueOf(23))
                .volume(60.0)
                .portfolio("portfolio2")
                .action(Trade.Action.NEW)
                .account("account1")
                .strategy("strategy6")
                .user("user2")
                .tradeTime(Instant.now())
                .valueDate("20220325")
                .build();

        HttpEntity<AddTradeRequestDto> request = new HttpEntity<>(requestDto);
        ResponseEntity<AddTradeResponseDto> response = this.testRestTemplate.postForEntity(uri, request, AddTradeResponseDto.class);

        assertEquals(200, response.getStatusCodeValue());

        AddTradeResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(requestDto.getSide(), responseBody.getSide());
        assertEquals(requestDto.getPrice(), responseBody.getPrice());
        assertEquals(requestDto.getPortfolio(), responseBody.getPortfolio());
        assertEquals(requestDto.getAccount(), responseBody.getAccount());
        assertEquals(requestDto.getTradeTime(), responseBody.getTradeTime());
        assertEquals(requestDto.getStrategy(), responseBody.getStrategy());
    }

    @Test
    void computePositionAggregation() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/trade/position/aggregate";
        URI uri = new URI(baseUrl);
        ResponseEntity<Map> response = this.testRestTemplate.getForEntity(uri, Map.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Map<String, LinkedHashMap<String, Double>>> positionAggregationResult = (Map<String, Map<String, LinkedHashMap<String, Double>>>) response.getBody();
        assertNotNull(positionAggregationResult);

        //Assert 6 difference BBGCode
        System.out.println("\nUnique BBG Codes: " + new ArrayList<>(positionAggregationResult.keySet()) + "\n");
        assertEquals(7, positionAggregationResult.entrySet().size());

        //Expected BBG Code
        List<String> expectedBbgCode = List.of("QQQ US ETF", "BC94 JPY Equity", "AAPL US Equity", "GOOG US Equity", "V LN Equity", "BRK.A US Equity");

        //Assert all BBG code are present in the returned result
        expectedBbgCode.forEach(bbgCode -> assertTrue(positionAggregationResult.containsKey(bbgCode)));

        // Pick one BBGCode and Analyze //
        //1. BBGCode="QQQ US ETF" must have summary for "portfolio1"
        Map<String, LinkedHashMap<String, Double>> portfoliosSummary = positionAggregationResult.get("QQQ US ETF");
        assertNotNull(portfoliosSummary);
        LinkedHashMap<String, Double> portfolio1PositionSummary = portfoliosSummary.get("portfolio1");
        assertNotNull(portfolio1PositionSummary);

        //2. Portfolio1 MUST have this: Profit=1390.47 & Loss=0
        assertEquals(BigDecimal.valueOf(1390.47), BigDecimal.valueOf(portfolio1PositionSummary.get("profit")));
        assertEquals(new BigDecimal("0.0"), BigDecimal.valueOf(Double.parseDouble(String.valueOf(portfolio1PositionSummary.get("loss")))));
    }

}