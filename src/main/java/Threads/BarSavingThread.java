package Threads;

import Libraries.StockCalculationLibrary;
import Model.Bar;
import Model.ConfigurationTest;
import Model.Ticker;
import Repository.DatabaseBarRepository;
import Util.DateFormatter;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BarSavingThread extends Thread {
    int threadNum;
    BarSavingThreadMonitor barSavingThreadMonitor;
    DatabaseBarRepository databaseBarRepository;
    List<Ticker> tickerList;
    LocalDate priorStartDate;
    LocalDate priorEndDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    LocalDate fiveDaysBack;

    // boolean ohShit;
    public BarSavingThread(List<Ticker> tickerList, DatabaseBarRepository databaseBarRepository, LocalDate priorStartDate, LocalDate priorEndDate, BarSavingThreadMonitor barSavingThreadMonitor) {
        this.tickerList = tickerList;
        this.databaseBarRepository = databaseBarRepository;
        this.priorStartDate = priorStartDate;
        this.priorEndDate = priorEndDate;
        this.fiveDaysBack = priorEndDate.minusDays(5);
        this.barSavingThreadMonitor = barSavingThreadMonitor;
        ///  this.ohShit = ohShit;
    }


    @Override
    public void run() {
        int count = 0;
        JSONObject treasuryDataJson;
        JSONArray dateArray;
        JSONArray rateArray;
        JSONArray vixDateArray;
        JSONArray vixRateArray;
        JSONArray bcomDateArray;
        JSONArray bcomRateArray;
        JSONArray dollarDateArray;
        JSONArray dollarRateArray;
        JSONArray oilDateArray;
        JSONArray oilRateArray;

        JSONArray tenYearDateArray;
        JSONArray tenYearRateArray;
        JSONArray goldDateArray;
        JSONArray goldRateArray;
        while (true) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                                "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22" +
                                "IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22" +
                                "IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22" +
                                "ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                                "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                                "Series%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD01M%22%2C%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22" +
                                "Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                treasuryDataJson = new JSONObject(response.body());
                dateArray = treasuryDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                rateArray = treasuryDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");


                Connection.Response vixResponse = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?" +
                                "json=%7B%22Step%22%3A%22P1D%22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A" +
                                "%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22" +
                                "%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22" +
                                "%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C" +
                                "%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C" +
                                "%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series%22%3A%5B%7B%22Key%22%3A%22INDEX%2FUS%2FCBSX%2FVIX%22%2C%22" +
                                "Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject vixData = new JSONObject(vixResponse.body());
                vixDateArray = vixData.getJSONObject("TimeInfo").getJSONArray("Ticks");
                vixRateArray = vixData.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");


                Connection.Response dollarResponse = Jsoup.connect("https://api.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%" +
                                "22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3A" +
                                "true%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22" +
                                "IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22" +
                                "UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22" +
                                "ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series%22%3A%5B%7B%22Key%22%3A%22INDEX%2FUS%2FIFUS%2FDXY" +
                                "%22%2C%22Dialect%22%3A%22Charting%22%2C%22" +
                                "Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();

                JSONObject dollarDataJson = new JSONObject(dollarResponse.body());
                dollarDateArray = dollarDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                dollarRateArray = dollarDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");

                Connection.Response oilResponse = Jsoup.connect("https://api.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22TimeFrame%22%3A%22ALL%22" +
                                "%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series%22%3A%5B%7B%22Key%22%3A%22FUTURE%2FUS%2FXNYM%2FCL.1%22%2C%22Dialect%22%3A%22Charting%22%2C" +
                                "%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject oilDataJson = new JSONObject(oilResponse.body());
                oilDateArray = oilDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                oilRateArray = oilDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");

                Connection.Response bcomResponse = Jsoup.connect("https://api.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D" +
                                "%22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken" +
                                "%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick" +
                                "%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints" +
                                "%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose" +
                                "%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours" +
                                "%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes" +
                                "%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series" +
                                "%22%3A%5B%7B%22Key%22%3A%22INDEX%2FXX%2F%2FBCOM%22%2C%22Dialect" +
                                "%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes" +
                                "%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject bcomDataJson = new JSONObject(bcomResponse.body());
                bcomDateArray = bcomDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                bcomRateArray = bcomDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");


                Connection.Response tenYearResponse = Jsoup.connect("https://api.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D" +
                                "%22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken" +
                                "%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick" +
                                "%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints" +
                                "%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose" +
                                "%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours" +
                                "%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes" +
                                "%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series" +
                                "%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD10Y%22%2C%22Dialect" +
                                "%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes" +
                                "%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject tenYearDataJson = new JSONObject(tenYearResponse.body());
                tenYearDateArray = tenYearDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                tenYearRateArray = tenYearDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");


                Connection.Response goldResponse = Jsoup.connect("https://api.wsj.net/api/michelangelo/timeseries" +
                                "/history?json=%7B%22Step%22%3A%22P1D%22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22" +
                                "ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose" +
                                "%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange" +
                                "%22%3Afalse%2C%22Series%22%3A%5B%7B%22Key%22%3A%22FUTURE%2FUS%2FXNYM%2FGC00%22%2C%22Dialect" +
                                "%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataType" +
                                "s%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                        .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject goldDataJson = new JSONObject(goldResponse.body());
                goldDateArray = goldDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
                goldRateArray = goldDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");

                break;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

        }
        for (Ticker uniqueTicker : tickerList) {


            List<Bar> barsToSave = getBarsBetweenDates(priorStartDate, priorEndDate, uniqueTicker.getTicker());


            if (barsToSave != null) {
                if (barsToSave.size() > 0) {
                    for (Bar bar : barsToSave) {
                        bar.setTicker(uniqueTicker.getTicker());
                        bar.setCik(uniqueTicker.getCik());
                    }

                    if (!isDayComplete()) {
                        if (barsToSave.get(barsToSave.size() - 1).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now())) {
                            barsToSave.get(barsToSave.size() - 1).setVolume(barsToSave.get(barsToSave.size() - 1).getVolume() * (1 / calculateDayPercentageComplete()));
                        }
                    }
                    double sharesOutstanding = 0.0;
                    // if(!ohShit) {
                    for (int i = 0; i < barsToSave.size(); i++) {
                        //if(uniqueTicker.getType().equals("STOCK")) {
                        if (i % 50 == 0) {
                            while (true) {
                                Connection.Response financialResponse = null;
                                try {
                                    String url = "https://api.polygon.io/v3/reference/tickers/" + uniqueTicker.getTicker()
                                            + "?date=" + barsToSave.get(i).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "&apiKey=";
                                    financialResponse = Jsoup.connect
                                                    (url)
                                            .method(Connection.Method.GET)
                                            .ignoreContentType(true)
                                            .ignoreHttpErrors(true)
                                            .execute();

                                    JSONObject financialsObject = new JSONObject(financialResponse.body());
                                    if (financialsObject.has("results")) {
                                        JSONObject financialsArray = financialsObject.getJSONObject("results");

                                        if (financialsArray.has("share_class_shares_outstanding")) {
                                            sharesOutstanding = financialsArray.getDouble("share_class_shares_outstanding");
                                        } else if (financialsArray.has("weighted_shares_outstanding")) {
                                            sharesOutstanding = financialsArray.getDouble("weighted_shares_outstanding");
                                        }
                                    }
                                    break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        barsToSave.get(i).setSharesOutstanding(sharesOutstanding);
                        barsToSave.get(i).setMarketCap(sharesOutstanding * barsToSave.get(i).getClose());
                        // }
                    }
                    //  }
                    //else{

                    //   }
                    List<LocalDate> localDateList = new ArrayList<>();
                    while (true) {
                        try {
                            String financialString = "https://api.polygon.io/vX/reference/financials" +
                                    "?ticker=" + uniqueTicker.getTicker() + "&limit=100&filing_date.gte=" + DateFormatter.formatLocalDate(priorStartDate.minusYears(1).minusMonths(6)) + "&filing_date.lt=" + DateFormatter.formatLocalDate(priorEndDate) + "&apiKey=";
                            Connection.Response financialResponse = Jsoup.connect
                                            (financialString)
                                    .method(Connection.Method.GET)
                                    .ignoreContentType(true)
                                    .execute();

                            JSONObject financialsObject = new JSONObject(financialResponse.body());
                            JSONArray financialsArray = financialsObject.getJSONArray("results");

                            for (int x = 0; x < financialsArray.length(); x++) {

                                String dateString = financialsArray.getJSONObject(x).getString("filing_date");
                                LocalDate filingLocalDate = sdf.parse(dateString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                localDateList.add(filingLocalDate);
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//
                    JSONObject jsonObject4;
                    JSONArray resultArray4;
                    String url4 = "https://api.polygon.io/v3/reference/splits?ticker=" + uniqueTicker.getTicker() + "&apiKey=";
                    while (true) {
                        try {
                            Connection.Response response4 = Jsoup.connect
                                            (url4)
                                    .method(Connection.Method.GET)
                                    .ignoreContentType(true)
                                    .execute();

                            jsonObject4 = new JSONObject(response4.body());
                            resultArray4 = jsonObject4.getJSONArray("results");
                            break;
                        } catch (Exception ignored) {

                        }
                    }

                    for (Object object : resultArray4) {
                        if (!resultArray4.isEmpty()) {
                            JSONObject resultObject = (JSONObject) object;
                            System.out.println(resultObject);
                            String dateString = resultObject.getString("execution_date");
                            Date date = null;
                            try {
                                date = sdf.parse(dateString);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            double multiple = (double) resultObject.getInt("split_to") / resultObject.getInt("split_from");
                            for (Bar bar1 : barsToSave) {
                                if (bar1.getDate() == date || bar1.getDate().before(date)) {
                                    bar1.setSplitAdjustFactor(bar1.getSplitAdjustFactor() * multiple);
                                }
                            }
                        }
                    }
                    for (Bar bar : barsToSave) {
                        LocalDate barLocalDate = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        for (LocalDate localDate : localDateList) {
                            if (barLocalDate.equals(localDate)) {
                                bar.setEarningsDate(true);
                                break;
                            }
                        }
                    }
//                                StockCalculationLibrary stockCalculationLibrary = new StockCalculationLibrary(null,null);
//                                int size = barsToSave.size();
//                                for (int z = 0; z < size; z++) {
//                                    barsToSave.get(z).setBaseVolatility(stockCalculationLibrary.getLogVarianceReverse(barsToSave, z, 30 + 1, uniqueTicker.getTicker()));
//                                    barsToSave.get(z).setBaseLongVolatility(stockCalculationLibrary.getLogVarianceReverse(barsToSave, z, 90 + 1, uniqueTicker.getTicker()));
//                                }

                    System.out.println("saving " + uniqueTicker + "\t" + count + "/" + tickerList.size());
                    for (Bar bar : barsToSave) {

                        Optional<Bar> temp = databaseBarRepository.findByTickerAndCikAndDate(uniqueTicker.getTicker(), uniqueTicker.getCik(), bar.getDate());
                        for (int x = 1; x < oilDateArray.length(); x++) {
                            if (oilDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && oilRateArray.getJSONArray(x).get(0) != null && oilRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setOilValue(oilRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < bcomDateArray.length(); x++) {
                            if (bcomDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && bcomRateArray.getJSONArray(x).get(0) != null && bcomRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setCommodityIndexValue(bcomRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < dollarDateArray.length(); x++) {
                            if (dollarDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && dollarRateArray.getJSONArray(x).get(0) != null && dollarRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setDollarRate(dollarRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < dateArray.length(); x++) {
                            //if (dateArray.getLong(x) < (bar.getDate().getTime()-(60000*1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL && twoYearRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                            if (dateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setTreasuryRate(rateArray.getJSONArray(x).getDouble(0));

                                //bar.setYieldCurveValue(rateArray.getJSONArray(x).getDouble(0) - twoYearRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < vixDateArray.length(); x++) {
                            if (vixDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && vixRateArray.getJSONArray(x).get(0) != null && vixRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                // bar.setVixValue(vixRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < tenYearDateArray.length(); x++) {
                            if (tenYearDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && tenYearRateArray.getJSONArray(x).get(0) != null && tenYearRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setTenYearRate(tenYearRateArray.getJSONArray(x).getDouble(0));
                            }
                        }
                        for (int x = 1; x < goldDateArray.length(); x++) {
                            if (goldDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && goldRateArray.getJSONArray(x).get(0) != null && goldRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                bar.setGoldValue(goldRateArray.getJSONArray(x).getDouble(0));
                            }
                        }


                        if (temp.isEmpty()) {

                            for (int x = 1; x < dateArray.length(); x++) {
                                //if (dateArray.getLong(x) < (bar.getDate().getTime()-(60000*1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL && twoYearRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                if (dateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                    bar.setTreasuryRate(rateArray.getJSONArray(x).getDouble(0));

                                    //bar.setYieldCurveValue(rateArray.getJSONArray(x).getDouble(0) - twoYearRateArray.getJSONArray(x).getDouble(0));
                                }
                            }

                            for (int x = 1; x < vixDateArray.length(); x++) {
                                if (vixDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && vixRateArray.getJSONArray(x).get(0) != null && vixRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                    //   bar.setVixValue(vixRateArray.getJSONArray(x).getDouble(0));
                                }
                            }

                            for (int x = 1; x < dollarDateArray.length(); x++) {
                                if (dollarDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && dollarRateArray.getJSONArray(x).get(0) != null && dollarRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                                    bar.setDollarRate(dollarRateArray.getJSONArray(x).getDouble(0));
                                }
                            }


                            databaseBarRepository.save(bar);


                        } else {
                            //if(temp.isPresent()){
                            Bar tempBar = temp.get();
                            //if (tempBar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(fiveDaysBack)) {
                            tempBar.setOpen(bar.getOpen());
                            tempBar.setClose(bar.getClose());
                            //tempBar.setVwap(bar.getVwap());
                            tempBar.setVolume(bar.getVolume());
                            tempBar.setLow(bar.getLow());
                            tempBar.setHigh(bar.getHigh());
                            tempBar.setOilValue(bar.getOilValue());
                            tempBar.setCommodityIndexValue(bar.getCommodityIndexValue());
                            tempBar.setTreasuryRate(bar.getTreasuryRate());
                            tempBar.setDollarRate(bar.getDollarRate());
                            tempBar.setEarningsDate(bar.isEarningsDate());
                            // tempBar.setVixValue(bar.getVixValue());
                            //    System.out.println("saving " + bar.getDate() + "\t" + bar.getTicker() + "\t" + count + "/" + tickerList.size());
                            databaseBarRepository.save(tempBar);
                            //   }

                            //}
                            //   System.out.println("no change to  " + bar.getDate() + "\t" + bar.getTicker() + "\t" + count + "/" + tickerList.size());
                        }
                    }
                    count++;
                }
            }

            List<Bar> uniqueBarTickers =
                    databaseBarRepository.findAllByCikAndTickerAndDateAfterAndDateBefore(uniqueTicker.getCik(), uniqueTicker.getTicker(), Date.from(priorStartDate.minusMonths(6).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            uniqueBarTickers.sort(Comparator.comparing(Bar::getDate));

            // LOG REVERSE
            int completedSize = uniqueBarTickers.size();
            StockCalculationLibrary stockCalculationLibrary = new StockCalculationLibrary();
            for (int z = 0; z < completedSize; z++) {
                uniqueBarTickers.get(z).setBaseVolatility(stockCalculationLibrary.getLogVarianceReverse(uniqueBarTickers, z, 63 + 1, uniqueTicker.getTicker()));
                uniqueBarTickers.get(z).setBaseLongVolatility(stockCalculationLibrary.getLogVarianceReverse(uniqueBarTickers, z, 90 + 1, uniqueTicker.getTicker()));
            }
            Collections.reverse(uniqueBarTickers);
            int size = uniqueBarTickers.size();
            try {
                for (int x = 0; x < size; x++) {
                    stepFour(stockCalculationLibrary, uniqueBarTickers, uniqueBarTickers.get(x), x, 9, true, 0, true);
                    //  stepFour(stockCalculationLibrary, barList, barList.get(x), x, configurationTest.getCorrelationDays(), false, lowBetaIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("");

            calculateDaysVolTriggerSlope(uniqueBarTickers, (int) 3, (int) 13, (int) 60, true);
            getVolumeChange(uniqueBarTickers, 11);
            calcCdfs(uniqueBarTickers);
            databaseBarRepository.saveAll(uniqueBarTickers);
        }
        barSavingThreadMonitor.threadFinished();
    }

    public void getVolumeChange(List<Bar> bars, double test) {
        for (int z = 0; z < bars.size(); z++) {
            if (bars.get(z).getMarketCap() > 50000000000L) {
                test = test;
            }
            if (z + test < bars.size()) {

                bars.get(z).setVolume(bars.get(z).getVolume());
                double sum = 0.0;
                for (int x = 0; x < test / 2; x++) {
                    sum += bars.get(z + x).getVolume();
                }
                sum = sum / (test / 2);
                double oldSum = 0.0;
                for (int x = (int) (test / 2); x < test; x++) {
                    oldSum += bars.get(z + x).getVolume();
                }
                oldSum = oldSum / (int) (test / 2);

                bars.get(z).setVolumeChange((sum - oldSum) / oldSum);
            }

        }
    }
    public void calcCdfs(List<Bar> barList) {
        for (int x = 0; x < barList.size(); x++) {
            Bar bar = barList.get(x);
            double cdfValue = 0.0;
            double roccdf = 0.0;
            double volcdf = 0.0;
            double volrocdf = 0.0;
            double volumeCdf = 0.0;
            List<Float> distDoubles = new ArrayList<>();
            List<Float> rocDoubles = new ArrayList<>();
            List<Double> volDoubles = new ArrayList<>();
            List<Double> volRocDoubles = new ArrayList<>();
            List<Double> volumeDoubles = new ArrayList<>();
            try {
                if (x > 30) {
                    double y = bar.getSignalSlopeLong(); // Example value
                    double p = bar.getSignalRocLong();
                    double v = bar.getVolatilitySlopeLong();
                    double vs = bar.getVolatilitySlopeRoCLong();
                    double volume = bar.getVolumeChange();
                    for (int j = x; j < barList.size(); j++) {
                        if (barList.get(j).getDate().getTime() < bar.getDate().getTime()) {
                            if (distDoubles.size() < 30 && barList.get(j).getSignalSlopeLong() != 0.0) {
                                distDoubles.add(barList.get(j).getSignalSlopeLong());
                                rocDoubles.add(barList.get(j).getSignalRocLong());
                                volDoubles.add(barList.get(j).getVolatilitySlopeLong());
                                volRocDoubles.add(barList.get(j).getVolatilitySlopeRoCLong());
                                volumeDoubles.add(barList.get(j).getVolumeChange());
                            }
                        }
                    }
                    if (distDoubles.size() == 30) {
                        double standardDeviation = 0.0;
                        double rocstddev = 0.0;
                        double volStddev = 0.0;
                        double volRocStddev = 0.0;
                        double volumeStddev = 0.0;
                        DoubleSummaryStatistics distSummaryStatistics = distDoubles.stream().collect(Collectors.summarizingDouble(e -> e));
                        DoubleSummaryStatistics rocSummaryStatistics = rocDoubles.stream().collect(Collectors.summarizingDouble(e -> e));
                        DoubleSummaryStatistics volSummaryStatistics = volDoubles.stream().collect(Collectors.summarizingDouble(e -> e));
                        DoubleSummaryStatistics volRocSummaryStatistics = volRocDoubles.stream().collect(Collectors.summarizingDouble(e -> e));
                        DoubleSummaryStatistics volumeSummaryStatistics = volumeDoubles.stream().collect(Collectors.summarizingDouble(e -> e));
                        for (double num : distDoubles) {
                            standardDeviation += Math.pow(num - distSummaryStatistics.getAverage(), 2);
                        }
                        for (double num : rocDoubles) {
                            rocstddev += Math.pow(num - rocSummaryStatistics.getAverage(), 2);
                        }
                        for (double num : volDoubles) {
                            volStddev += Math.pow(num - volSummaryStatistics.getAverage(), 2);
                        }
                        for (double num : volRocDoubles) {
                            volRocStddev += Math.pow(num - volRocSummaryStatistics.getAverage(), 2);
                        }
                        for (double num : volumeDoubles) {
                            volumeStddev += Math.pow(num - volumeSummaryStatistics.getAverage(), 2);
                        }
                        NormalDistribution distribution = new NormalDistribution(distSummaryStatistics.getAverage(), Math.sqrt(standardDeviation / distDoubles.size()));
                        NormalDistribution rocdist = new NormalDistribution(distSummaryStatistics.getAverage(), Math.sqrt(rocstddev / rocDoubles.size()));
                        NormalDistribution volumedist = new NormalDistribution(distSummaryStatistics.getAverage(), Math.sqrt(volumeStddev / rocDoubles.size()));
                        if (volStddev != 0.0) {
                            NormalDistribution voldist = new NormalDistribution(volSummaryStatistics.getAverage(), Math.sqrt(volStddev / volDoubles.size()));
                            volcdf = voldist.cumulativeProbability(v);
                        }
                        if (volRocStddev != 0.0) {
                            NormalDistribution volrocdist = new NormalDistribution(volRocSummaryStatistics.getAverage(), Math.sqrt(volRocStddev / volRocDoubles.size()));
                            volrocdf = volrocdist.cumulativeProbability(vs);
                        }
                        cdfValue = distribution.cumulativeProbability(y);
                        roccdf = rocdist.cumulativeProbability(p);
                        volumeCdf = volumedist.cumulativeProbability(volume);
                        bar.setSignalcdf((float) cdfValue);
                        bar.setSignalroccdf((float) roccdf);
                        bar.setVolumecdf((float) volumeCdf);
                        bar.setVolroccdf((float) volrocdf);
                        bar.setVolcdf((float) volcdf);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void calculateDaysVolTriggerSlope(List<Bar> barList, int daysSlope, int secondSlopeDays, int volatilitySlopeDays, boolean isLong){

        for(int i = 0; i < barList.size(); i++) {
            int modDaySlope = daysSlope;
            int modSecondSlope = secondSlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modDaySlope = modDaySlope;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modDaySlope = modDaySlope;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modDaySlope = (int) (modDaySlope*(300*0.01));
                modSecondSlope = (int) (modSecondSlope*(300*0.01));
            }
            if(i < barList.size() - modDaySlope){
                List<Long> dateLongs = new ArrayList<>();
                List<Double> oneMonthDoubles = new ArrayList<>();
                List<Double> treasuryDoubles = new ArrayList<>();
                List<Double> oilDoubles = new ArrayList<>();
                List<Double> commodityDoubles = new ArrayList<>();
                List<Double> closeDoubles = new ArrayList<>();
                List<Double> tenYearDoubles = new ArrayList<>();
                List<Double> goldDoubles = new ArrayList<>();
                for(int x = 1; x <= modDaySlope; x++){
                    oneMonthDoubles.add(barList.get(i + x).getDollarCorrelationFactor());
                    treasuryDoubles.add(barList.get(i + x).getTreasuryCorrelationFactor());
                    oilDoubles.add(barList.get(i + x).getOilCorrelationFactor());
                    commodityDoubles.add(barList.get(i + x).getCommodityIndexValue());
                    dateLongs.add(barList.get(i + x).getDate().getTime());
                    closeDoubles.add(barList.get(i + x).getClose());
                    tenYearDoubles.add(barList.get(i+x).getTenYearCorrelationFactor());
                    goldDoubles.add(barList.get(i+x).getGoldCorrelationFactor());
                }
                SimpleRegression dollarRegression = new SimpleRegression();
                SimpleRegression treasuryRegression = new SimpleRegression();
                SimpleRegression oilRegression = new SimpleRegression();
                SimpleRegression comoddityRegression = new SimpleRegression();
                SimpleRegression closeRegression = new SimpleRegression();
                SimpleRegression goldRegression = new SimpleRegression();
                SimpleRegression tenYearRegression = new SimpleRegression();
                for(int z = 0; z <dateLongs.size(); z++){
                    dollarRegression.addData(dateLongs.get(z),oneMonthDoubles.get(z));
                    treasuryRegression.addData(dateLongs.get(z),treasuryDoubles.get(z));
                    oilRegression.addData(dateLongs.get(z),oilDoubles.get(z));
                    comoddityRegression.addData(dateLongs.get(z),commodityDoubles.get(z));
                    closeRegression.addData(dateLongs.get(z),closeDoubles.get(z));
                    goldRegression.addData(dateLongs.get(z),goldDoubles.get(z));
                    tenYearRegression.addData(dateLongs.get(z),tenYearDoubles.get(z));
                }

                barList.get(i).setPriceSlope(closeRegression.getSlope());
                barList.get(i).setTreasuryYieldSlope(treasuryRegression.getSlope());
                barList.get(i).setOilSlope(oilRegression.getSlope());
                barList.get(i).setCommoditySlope(comoddityRegression.getSlope());
                barList.get(i).setDollarSlope(dollarRegression.getSlope());
                barList.get(i).setGoldSlope(goldRegression.getSlope());
                barList.get(i).setTenYearSlope(tenYearRegression.getSlope());
                if(isLong) {
                    barList.get(i).setSignalSlopeLong((float) (dollarRegression.getSlope() + treasuryRegression.getSlope() + comoddityRegression.getSlope()));
                  //  barList.get(i).setAlternateSignalSlope(goldRegression.getSlope() + tenYearRegression.getSlope());
                }

            }
        }
        try{
            for(int i = 0; i < barList.size(); i++) {
                int modDaySlope = daysSlope;
                int modSecondSlope = secondSlopeDays;
                if (barList.get(i).getMarketCap() < 2000000000) {
                    modDaySlope = modDaySlope;
                } else if (barList.get(i).getMarketCap() < 50000000000L) {
                    modDaySlope = modDaySlope;
                } else if (barList.get(i).getMarketCap() > 50000000000L) {
                    modDaySlope = modDaySlope * 2;
                    modSecondSlope = (int) (modSecondSlope * (300 * 0.01));
                }
                if (i < barList.size() - modSecondSlope) {
                    SimpleRegression priceSlopeRegression = new SimpleRegression();
                    SimpleRegression simpleRegression = new SimpleRegression();
                    SimpleRegression alternateRegression = new SimpleRegression();
                    SimpleRegression treasuryRocRegression = new SimpleRegression();
                    SimpleRegression dollarRocRegression = new SimpleRegression();
                    SimpleRegression oilRocRegression = new SimpleRegression();
                    SimpleRegression commodityRocRegression = new SimpleRegression();
                    SimpleRegression goldRocRegression = new SimpleRegression();
                    SimpleRegression tenYearRocRegression = new SimpleRegression();
                    for (int x = 0; x <= modSecondSlope - 1; x++) {
                        if (isLong) {
                            simpleRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getSignalSlopeLong());
                          //  alternateRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getAlternateSignalSlope());
                            treasuryRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getTreasuryYieldSlope());
                            dollarRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getDollarSlope());
                            oilRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getOilSlope());
                            commodityRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getCommoditySlope());
                            goldRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getGoldSlope());
                            tenYearRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getTenYearSlope());
                        } else {
                            //simpleRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getSignalSlopeShort());
                            treasuryRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getTreasuryYieldSlope());
                            dollarRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getDollarSlope());
                        }
                        if (isLong) {
                            priceSlopeRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getPriceSlope());
                        } else {
                            priceSlopeRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getPriceSlope());
                        }

                    }
                    barList.get(i).setTreasuryYieldSlopeRoc(treasuryRocRegression.getSlope());
                    barList.get(i).setDollarSlopeRoc(dollarRocRegression.getSlope());
                    barList.get(i).setCommodityRateOfChange(commodityRocRegression.getSlope());
                    barList.get(i).setOilSlopeRoc(oilRocRegression.getSlope());
                    barList.get(i).setGoldSlopeRoc(goldRocRegression.getSlope());
                    barList.get(i).setTenYearSlopeRoc(tenYearRocRegression.getSlope());
                    if (isLong) {
                        barList.get(i).setPriceSlopeRoc(priceSlopeRegression.getSlope());
                        barList.get(i).setSignalRocLong((float) simpleRegression.getSlope());
                      //  barList.get(i).setAlternateSignalSlopeRoc(alternateRegression.getSlope());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        for(int i = 0; i < barList.size(); i++) {
//            if(i < barList.size() - volatilitySlopeDays){
//                List<Double> vixDoubles = new ArrayList<>();
//                List<Long> dateLongs = new ArrayList<>();
//                for(int x = 1; x <= volatilitySlopeDays; x++){
//                    dateLongs.add(barList.get(i + x).getDate().getTime());
//                    vixDoubles.add(barList.get(i+x).getVixValue());
//                }
//                SimpleRegression volumeRegression = new SimpleRegression();
//                for(int z = 0; z <dateLongs.size(); z++){
//                    volumeRegression.addData(dateLongs.get(z),vixDoubles.get(z));
//                }
//                if(isLong){
//                    barList.get(i).setVixRoCLong(volumeRegression.getSlope());
//                }else {
//                    barList.get(i).setVixRoCShort(volumeRegression.getSlope());
//                }
//            }
//        }
        for(int i = 0; i < barList.size(); i++) {

            int modVolSlopeDays = volatilitySlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modVolSlopeDays = (int) (modVolSlopeDays*(300*0.01));

            }
            if (i < barList.size() - modVolSlopeDays) {
                SimpleRegression volatilityRegression = new SimpleRegression();
                for (int x = 0; x <= modVolSlopeDays - 1; x++) {
                    if(barList.get(i + (modVolSlopeDays - x)).getBaseVolatility() != 0){
                        volatilityRegression.addData((modVolSlopeDays - x), barList.get(i + (modVolSlopeDays - x)).getBaseVolatility());
                    }
                }
                if(volatilityRegression.getN() == modVolSlopeDays) {
                    if (isLong) {
                        barList.get(i).setVolatilitySlopeLong(volatilityRegression.getSlope());
                    }
                }
            }
        }
        //ALTERNATIVELY TRYING DIFFERENT VARIABLE THAN VOLAILITITYSLOPEDAYS
        for(int i = 0; i < barList.size(); i++) {
            int modVolSlopeDays = volatilitySlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modVolSlopeDays = (int) (modVolSlopeDays*(300*0.01));

            }
            if (i < barList.size() - modVolSlopeDays) {
                SimpleRegression volatilityRoCRegression = new SimpleRegression();
                for (int x = 0; x <= modVolSlopeDays - 1; x++) {
                    if(barList.get(i + (modVolSlopeDays - x)).getVolatilitySlopeLong() != 0) {
                        if (isLong) {
                            volatilityRoCRegression.addData((modVolSlopeDays - x), barList.get(i + (modVolSlopeDays - x)).getVolatilitySlopeLong());
                        }
                    }
                }
                if(isLong) {
                    Double test = volatilityRoCRegression.getSlope();
                    barList.get(i).setVolatilitySlopeRoCLong(test);
                }
            }
        }
    }
    private void stepFour(StockCalculationLibrary stockCalculationLibrary,  List<Bar> barList, Bar bar,
                          int index, int correlationDays1, boolean calculatingTrend, int lowBetaIndex, boolean isLong) {
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        // int lookback = bar.getLookback();
        int correlationDays = 53;

        if (correlationDays > 1) {
            List<Double> priceChanges = new ArrayList<>();
            //    List<Double> historicalVolChanges = new ArrayList<>();
            List<Double> realizedVols = new ArrayList<>();
            List<Double> dollarChanges = new ArrayList<>();
            List<Double> oilChanges = new ArrayList<>();
            List<Double> commodityChanges = new ArrayList<>();
            List<Double> treasury = new ArrayList<>();
            List<Double> vix = new ArrayList<>();
            List<Double> gold = new ArrayList<>();
            List<Double> tenYear = new ArrayList<>();
            if (index + correlationDays + 14< barList.size()) {
                for (int k = index; k < index + correlationDays; k++) {
                    Bar current = barList.get(k);
                    // Bar past = barList.get(k + 1);
                    priceChanges.add(current.getClose());

                    realizedVols.add(current.getBaseVolatility());

                    dollarChanges.add(barList.get(k).getDollarRate());
                    oilChanges.add(barList.get(k).getOilValue());
                    treasury.add(barList.get(k).getTreasuryRate());
                    commodityChanges.add(barList.get(k).getCommodityIndexValue());
                    gold.add(barList.get(k).getGoldValue());
                    tenYear.add(barList.get(k).getTenYearRate());
                //    vix.add(barList.get(k).getVixValue());
                }
            }
            //double[] x = convertListToArray(volumes);
            //double[] y1 = convertListToArray(historicalVolChanges);
            //double[] x = volumes.stream().mapToDouble(z -> z).toArray();
            double[] y = convertListToArray(priceChanges);
            double[] x1 = convertListToArray(gold);
            double[] x2 = convertListToArray(tenYear);
            double[] x3 = convertListToArray(dollarChanges);
            double[] x4 = convertListToArray(realizedVols);
            double[] x5 = convertListToArray(treasury);
            double[] x6 = convertListToArray((commodityChanges));
            //double[] x7 = convertListToArray(fedAssetChanges);
            double[] x7 = convertListToArray(vix);
            double[] x8 = convertListToArray(oilChanges);

            if(calculatingTrend) {
                try {
                    if(!isLong) {
                        bar.setGoldCorrelationFactor(pearsonsCorrelation.correlation(x1,y));
                        bar.setTenYearCorrelationFactor(pearsonsCorrelation.correlation(x2,y));
                        bar.setDollarCorrelationFactor(pearsonsCorrelation.correlation(x3, y));
                        bar.setRealizedVolCorrelationFactor(pearsonsCorrelation.correlation(x4, y));
                        bar.setTreasuryCorrelationFactor(pearsonsCorrelation.correlation(x5, y));
                        bar.setCommodityCorrelationFactor(pearsonsCorrelation.correlation(x6, y));
                        bar.setOilCorrelationFactor(pearsonsCorrelation.correlation(x8, y));
                    //    bar.setVixCorrelationFactor(pearsonsCorrelation.correlation(x7, y));
                    }else{
                        bar.setGoldCorrelationFactor(pearsonsCorrelation.correlation(x1,y));
                        bar.setTenYearCorrelationFactor(pearsonsCorrelation.correlation(x2,y));
                        bar.setDollarCorrelationFactor(pearsonsCorrelation.correlation(x3, y));
                        bar.setRealizedVolCorrelationFactor(pearsonsCorrelation.correlation(x4, y));
                        bar.setTreasuryCorrelationFactor(pearsonsCorrelation.correlation(x5, y));
                        bar.setCommodityCorrelationFactor(pearsonsCorrelation.correlation(x6, y));
                        bar.setOilCorrelationFactor(pearsonsCorrelation.correlation(x8, y));
                    //    bar.setVixCorrelationFactor(pearsonsCorrelation.correlation(x7, y));
                    }

                } catch (Exception ignored) {
                    //   ignored.printStackTrace();
                }
            }else{
                try{

                } catch (Exception ignored) {
                }
            }
        }
    }
    public double[] convertListToArray(List<Double> originalList){
        double[] newArray = new double[originalList.size()];
        int size = originalList.size();
        for(int i = 0; i < size; i++){
            newArray[i] = originalList.get(i);
        }
        return newArray;
    }
    public List<Bar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
        while(true) {
            try {
                String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" +
                        DateFormatter.formatLocalDate(from) + "/" + DateFormatter.formatLocalDate(to) + "?adjusted=true&sort=asc&limit=5500" +
                        "&apiKey=";
                Connection.Response oilResponse = Jsoup.connect
                                (url)
                        .method(Connection.Method.GET)

                        .ignoreContentType(true)
                        .execute();
                JSONObject JsonObject = new JSONObject(oilResponse.body());
                if(JsonObject.has("results")) {
                    JSONArray resultArray = JsonObject.getJSONArray("results");
                    ArrayList<Bar> bars = new ArrayList<>();
                    for (Object object : resultArray) {
                        bars.add(buildBarFromJsonData(object));
                    }
                    return bars;
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public Bar buildBarFromJsonData(Object object){
        JSONObject resultObject = (JSONObject) object;
        Bar bar = new Bar();
        try {
            bar.setClose(resultObject.getDouble("c"));
            bar.setOpen(resultObject.getDouble("o"));
            bar.setHigh(resultObject.getDouble("h"));
            bar.setLow(resultObject.getDouble("l"));
            bar.setVolume(resultObject.getDouble("v"));
//            if(resultObject.has("vw")) {
//                bar.setVwap(resultObject.getDouble("vw"));
//            }
            bar.setDate(new Date(resultObject.getLong("t") + (60000 * 1440)));
            bar.setSplitAdjustFactor(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bar;
    }
    public double calculateDayPercentageComplete(){
        double minutes = 390;
        LocalDateTime now = LocalDateTime.now().minusMinutes(15);
        LocalDateTime startOfTrading = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 30);
        double minsBetween = ChronoUnit.MINUTES.between(startOfTrading, now);
        return minsBetween/minutes;
    }
    public boolean isDayComplete(){
        LocalDateTime now = LocalDateTime.now();
        if(now.getHour() >=17 && now.getMinute() >= 15){
            return true;
        }else{
            return false;
        }
    }
}
