package com.ghco.positioncalculator.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PositionAggregation {
    private final Map<String, List<BbgCodeAggregation>> result;
}
