package com.ghco.positioncalculator.util;

import com.ghco.positioncalculator.repository.CsvBackedTradeRepository;
import com.ghco.positioncalculator.repository.TradeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class BeanConfig {

    @Bean
    public TradeRepository csvBasedTradeRepository() {
        return new CsvBackedTradeRepository(new File("/Users/Akintunde/Documents/Software_Engineering/Learning/Backend/Framework/Spring_Boot/Mine/position-calculator/src/main/resources/static/trades.xlsx"));
    }

}
