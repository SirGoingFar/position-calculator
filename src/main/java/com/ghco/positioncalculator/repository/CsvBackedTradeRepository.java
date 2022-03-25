package com.ghco.positioncalculator.repository;

import com.ghco.positioncalculator.model.Trade;
import com.ghco.positioncalculator.util.ExcelFileUtil;
import lombok.NonNull;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

public class CsvBackedTradeRepository implements TradeRepository {

    private final Object TRADE_DATA_MONITOR_OBJECT = new Object();

    private final List<Trade> allTrades;

    public CsvBackedTradeRepository(@NonNull final File excelFile) {
        allTrades = readCsvFile(excelFile);
    }

    public CsvBackedTradeRepository(@NonNull final List<Trade> allTrades) {
        this.allTrades = allTrades;
    }

    @Override
    public List<Trade> findAll() {
        synchronized (TRADE_DATA_MONITOR_OBJECT) {
            return new ArrayList<>(allTrades);
        }
    }

    @Override
    public Trade save(@NonNull Trade trade) {
        synchronized (TRADE_DATA_MONITOR_OBJECT) {
            trade.setId(UUID.randomUUID().toString().replace("-", ""));
            allTrades.add(0, trade);
            return trade;
        }
    }

    @Override
    public List<String> findAllUniqueBbgCodes() {
        synchronized (TRADE_DATA_MONITOR_OBJECT) {
            return allTrades.stream().map(Trade::getBbgCode).distinct().sorted().collect(Collectors.toList());
        }
    }

    @Override
    public List<Trade> findAllByBbgCodeAndAction(@NonNull final String bbgCode, @NonNull final Trade.Action action) {
        synchronized (TRADE_DATA_MONITOR_OBJECT) {
            return allTrades.stream().filter(trade -> trade.getBbgCode().equals(bbgCode) && trade.getAction() == action).distinct().collect(Collectors.toList());
        }
    }

    @NonNull
    private List<Trade> readCsvFile(@NonNull final File excelFile) {
        synchronized (TRADE_DATA_MONITOR_OBJECT) {
            XSSFSheet worksheet = ExcelFileUtil.getWorksheetFromExcelFile(excelFile);
            if (worksheet == null) {
                throw new IllegalStateException("Invalid data file supplied");
            }

            int rowCount = Math.max(worksheet.getPhysicalNumberOfRows(), 1);

            List<Trade> trades = new ArrayList<>();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //UTC time

            String id;
            String bbgCode;
            String currency;
            Trade.Side side;
            BigDecimal price;
            Double volume;
            String portfolio;
            Trade.Action action;
            String account;
            String strategy;
            String user;
            Instant tradeTime = null;
            String valueDate;

            for (int row = 1; row <= rowCount; row++) {

                id = ExcelFileUtil.getXssfCellValue(worksheet, row, 0);
                if (id != null) {
                    id = id.trim();
                }

                if (!StringUtils.hasText(id)) {
                    break;
                }

                bbgCode = ExcelFileUtil.getXssfCellValue(worksheet, row, 1);
                if (bbgCode != null) {
                    bbgCode = bbgCode.trim();
                }

                currency = ExcelFileUtil.getXssfCellValue(worksheet, row, 2);
                if (currency != null) {
                    currency = currency.trim();
                }

                side = Trade.Side.of(ExcelFileUtil.getXssfCellValue(worksheet, row, 3));
                price = bigDecimalOf(ExcelFileUtil.getXssfCellValue(worksheet, row, 4));
                volume = doubleOf(ExcelFileUtil.getXssfCellValue(worksheet, row, 5));

                portfolio = ExcelFileUtil.getXssfCellValue(worksheet, row, 6);
                if (portfolio != null) {
                    portfolio = portfolio.trim();
                }

                action = Trade.Action.of(ExcelFileUtil.getXssfCellValue(worksheet, row, 7));

                account = ExcelFileUtil.getXssfCellValue(worksheet, row, 8);
                if (account != null) {
                    account = account.trim();
                }

                strategy = ExcelFileUtil.getXssfCellValue(worksheet, row, 9);
                if (strategy != null) {
                    strategy = strategy.trim();
                }

                user = ExcelFileUtil.getXssfCellValue(worksheet, row, 10);
                if (user != null) {
                    user = user.trim();
                }

                valueDate = ExcelFileUtil.getXssfCellValue(worksheet, row, 12);
                if (valueDate != null) {
                    if (valueDate.contains(".")) {
                        valueDate = valueDate.substring(0, valueDate.indexOf('.'));
                    }
                    valueDate = valueDate.trim();
                }

                if (StringUtils.hasText(valueDate)) {
                    String dc = String.format("%s-%s-%s", valueDate.substring(0, 4), valueDate.substring(4, 6), valueDate.substring(6));
                    try {
                        tradeTime = sdf.parse(dc).toInstant();
                    } catch (ParseException e) {
                        tradeTime = null;
                    }
                }

                trades.add(Trade.builder()
                        .id(id)
                        .bbgCode(bbgCode)
                        .currency(currency)
                        .side(side)
                        .price(price)
                        .volume(volume)
                        .portfolio(portfolio)
                        .action(action)
                        .account(account)
                        .strategy(strategy)
                        .user(user)
                        .tradeTime(tradeTime)
                        .valueDate(valueDate)
                        .build());
            }
            return trades;
        }
    }

    @Nullable
    private BigDecimal bigDecimalOf(@Nullable String value) {
        if (StringUtils.hasText(value)) {
            return new BigDecimal(value);
        }

        return null;
    }

    @Nullable
    private Double doubleOf(@Nullable String value) {
        if (StringUtils.hasText(value)) {
            return Double.parseDouble(value);
        }

        return null;
    }

}
