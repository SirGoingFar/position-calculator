package com.ghco.positioncalculator.util;

import com.ghco.positioncalculator.repository.CsvBackedTradeRepository;
import com.ghco.positioncalculator.repository.TradeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static com.ghco.positioncalculator.util.Constants.IS_TEST;

@Configuration
public class BeanConfig {

    @Bean
    public TradeRepository csvBasedTradeRepository() throws FileNotFoundException {
        File tradeDataFile = ResourceUtils.getFile("classpath:static/trades.xlsx");

        if(IS_TEST){
            tradeDataFile = ResourceUtils.getFile("classpath:static/trades-test-data.xlsx");
        }

        return new CsvBackedTradeRepository(tradeDataFile);
    }

}
