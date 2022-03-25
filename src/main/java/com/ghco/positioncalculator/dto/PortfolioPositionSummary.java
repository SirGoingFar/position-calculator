package com.ghco.positioncalculator.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PortfolioPositionSummary {
    private final BigDecimal profit;
    private final BigDecimal loss;
}
