package com.ghco.positioncalculator.controller;

import com.ghco.positioncalculator.dto.AddTradeRequestDto;
import com.ghco.positioncalculator.dto.AddTradeResponseDto;
import com.ghco.positioncalculator.dto.PositionAggregation;
import com.ghco.positioncalculator.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/trade")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/add")
    public ResponseEntity<AddTradeResponseDto> createTrade(@RequestBody @Valid AddTradeRequestDto requestDto) {
        return ResponseEntity.ok(tradeService.addTrade(requestDto));
    }

    @GetMapping("/position/aggregate")
    public ResponseEntity<PositionAggregation> computePositionAggregation() {
        return ResponseEntity.ok(tradeService.aggregateTradePosition());
    }

}
