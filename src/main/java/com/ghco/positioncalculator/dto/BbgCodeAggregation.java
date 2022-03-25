package com.ghco.positioncalculator.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BbgCodeAggregation {
    private final Map<String, BbgCodePositionSummary> summary;
}
